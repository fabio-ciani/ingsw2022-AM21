package it.polimi.ingsw.eriantys.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.client.cli.CommandLineInterface;
import it.polimi.ingsw.eriantys.client.gui.GraphicalUserInterface;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.client.*;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.model.AssistantCard;
import it.polimi.ingsw.eriantys.model.BoardStatus;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.TowerColor;
import it.polimi.ingsw.eriantys.model.Wizard;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * This is the main class for the client-side application.
 * It starts the {@link Socket}, connects to the server and handles received and sent messages.
 */
public class Client extends Thread {
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private final UserInterface ui;
	private String username;
	private Integer gameId;
	private String towerColor;
	private String wizard;
	private Integer characterCard;
	private List<String> availableCards;
	private BoardStatus boardStatus;

	/**
	 * The method is called to launch the client-side application (in either CLI or GUI mode).
	 * @param args the command line arguments
	 * @throws IOException if a {@link Client} object cannot be constructed
	 */
	public static void main(String[] args) throws IOException {
		String serverAddress = "localhost";
		int serverPort = 9133;
		boolean useGui = true;
		Options options = new Options();
		options.addOption(new Option("addr", "address", true, "Server address"));
		options.addOption(new Option("p", "port", true, "Server port"));
		options.addOption(new Option("ui", "interface", true, "User interface type"));
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("addr")) {
				serverAddress = line.getOptionValue("address");
			}
			if (line.hasOption("p")) {
				int port = Integer.parseInt(line.getOptionValue("port"));
				if (port >= 0 && port <= 65535)
					serverPort = port;
			}
			if (line.hasOption("ui")) {
				String type = line.getOptionValue("ui");
				if (type.equals("gui"))
					useGui = true;
				else if (type.equals("cli"))
					useGui = false;
			}
		}
		catch (ParseException e) {
			System.out.println("Parsing failed");
			System.exit(1);
		}
		Client client = new Client(serverAddress, serverPort, useGui);
		client.start();
	}

	/**
	 * Constructs a {@code Client} object, starting the {@link Socket}
	 * and either the {@link CommandLineInterface} or the {@link GraphicalUserInterface}.
	 * @param serverAddress the server IP address
	 * @param serverPort the port number identifying the server socket
	 * @param gui whether to start the GUI (if {@code true}) or the CLI (if {@code false})
	 * @throws IOException if the {@link Socket} could not be opened, the connection to the server could not be established,
	 * or a problem occurs while opening configuration files
	 */
	public Client(String serverAddress, int serverPort, boolean gui) throws IOException {
		try {
			this.ui = gui ? new GraphicalUserInterface() : new CommandLineInterface();
		} catch (IOException e) {
			throw new IOException("Unable to open characters.json", e);
		}
		try {
			this.socket = new Socket(serverAddress, serverPort);
			this.socket.setSoTimeout(10000);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			throw new IOException("Can't connect to the server", e);
		}
		this.running = true;
	}

	/**
	 * Starts the user interface thread and then loops to receive and handle messages from the server.
	 */
	@Override
	public void run() {
		ui.setClient(this);
		new Thread(ui).start();
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		ui.init();
		try (socket) {
			while (running) {
				Message message = (Message) in.readObject();
				handleMessage(message);
			}
		} catch (IOException | ClassNotFoundException e) {
			setRunning(false);
			ui.quit();
		}
	}

	/**
	 * Sends a message to the server.
	 * @param message the message to send
	 */
	public void write(Message message) {
		synchronized (out) {
			try {
				out.writeObject(message);
			} catch (IOException e) {
				setRunning(false);
				ui.quit();
			}
		}
	}

	/**
	 * Synchronized method to set the {@link #running} attribute.
	 * @param running the value to set
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * A getter for the username chosen by the player.
	 * @return the chosen username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * A setter for the {@link #username} attribute.
	 * @param username the username chosen by the player
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	private boolean usernameNotSet() {
		if (username == null) {
			ui.showError("Set a username first using\n /u, /user <username>");
			return true;
		}
		return false;
	}

	/**
	 * A setter for the {@link #gameId} attribute.
	 * @param gameId the identifier of the game the player joined
	 */
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	/**
	 * A getter for the {@link #towerColor} attribute.
	 * @return the {@link TowerColor} literal selected by the player
	 */
	public String getTowerColor() {
		return towerColor;
	}

	/**
	 * Sets the tower color selected by the player and tries to send the selection to the server.
	 * @param towerColor the selected {@link TowerColor} literal
	 */
	public void setTowerColor(String towerColor) {
		this.towerColor = towerColor;
		trySendSetupSelection();
	}

	/**
	 * A getter for the {@link #wizard} attribute.
	 * @return the {@link Wizard} literal selected by the player
	 */
	public String getWizard() {
		return wizard;
	}

	/**
	 * Sets the wizard selected by the player and tries to send the selection to the server.
	 * @param wizard the selected {@link Wizard} literal
	 */
	public void setWizard(String wizard) {
		this.wizard = wizard;
		trySendSetupSelection();
	}

	/**
	 * A getter for the last {@link BoardStatus} received from the server.
	 * If the {@link #boardStatus} is {@code null}, an error is shown via UI.
	 * @return the latest board status
	 */
	public BoardStatus getBoardStatus() {
		if (boardStatus == null) ui.showError("Nothing to show yet");
		return boardStatus;
	}

	/**
	 * A setter for the {@link #boardStatus} attribute.
	 * @param boardStatus the board status to set
	 */
	public void setBoardStatus(BoardStatus boardStatus) {
		this.boardStatus = boardStatus;
	}

	/**
	 * A getter for the list of the assistant cards that the player is allowed to play.
	 * @return the list of available assistant cards
	 */
	public List<String> getAvailableCards() {
		if (availableCards == null) ui.showError("Nothing to show yet");
		return availableCards;
	}

	/**
	 * A setter for the {@link #availableCards} attribute.
	 * @param availableCards the list of the assistant cards that the player is allowed to play
	 */
	public void setAvailableCards(List<String> availableCards) {
		this.availableCards = availableCards;
	}

	/**
	 * A getter for the {@link #characterCard} attribute.
	 * @return the character card selected by the player
	 */
	public Integer getCharacterCard() {
		return characterCard;
	}

	private void handleMessage(Message message) {
		if (message instanceof AcceptedUsername m) {
			ui.handleMessage(m);
		} else if (message instanceof AcceptedJoinLobby m) {
			ui.handleMessage(m);
		} else if (message instanceof AcceptedLeaveLobby m) {
			ui.handleMessage(m);
		} else if (message instanceof Accepted m) {
			ui.handleMessage(m);
		} else if (message instanceof RefusedReconnect m) {
			ui.handleMessage(m);
		} else if (message instanceof Refused m) {
			ui.handleMessage(m);
		} else if (message instanceof HelpResponse m) {
			ui.handleMessage(m);
		} else if (message instanceof AvailableLobbies m) {
			ui.handleMessage(m);
		} else if (message instanceof LobbyUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof UserSelectionUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof InitialBoardStatus m) {
			ui.handleMessage(m);
		} else if (message instanceof AssistantCardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof BoardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof CharacterCardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof LastRoundUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof GameOverUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof ReconnectionUpdate m) {
			towerColor = null; wizard = null;
			ui.handleMessage(m);
		} else if (message instanceof DisconnectionUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof Ping m) {
			ui.handleMessage(m);
		} else {
			ui.handleMessage(message);
		}
	}

	/**
	 * Sends a {@link HelpRequest} message to the server.
	 */
	public void askHelp() {
		if (usernameNotSet()) return;
		write(new HelpRequest(username));
	}

	/**
	 * Checks if the username is valid and sends a {@link Handshake} message to the server.
	 * @param username the username selected by the player
	 */
	public void sendHandshake(String username) {
		if(username.matches("^[a-zA-Z\\d]+(?:(?:-[a-zA-Z\\d]+)*|(?:\\.[a-zA-Z\\d]+)*)\\z") && username.length() <= 16) {
			write(new Handshake(username));
		} else {
			ui.showError("Invalid username, try again");
		}
	}

	/**
	 * Checks if a reconnection is available and sends a {@link Reconnect} message to the server.
	 */
	public void sendReconnect() {
		JsonObject reconnectSettings = getReconnectSettings();
		if (reconnectSettings == null) {
			ui.showError("Reconnection unavailable");
			return;
		}
		int gameId = reconnectSettings.get("gameId").getAsInt();
		String passcode = reconnectSettings.get("passcode").getAsString();
		write(new Reconnect(username, gameId, passcode));
	}

	/**
	 * Sends a {@link LobbiesRequest} message to the server.
	 */
	public void askLobbies() {
		if (usernameNotSet()) return;
		write(new LobbiesRequest(username));
	}

	/**
	 * Checks if the lobby id is a valid integer and sends a {@link JoinLobby} message to the server.
	 * @param lobbyIdArg the identifier of the lobby to join
	 */
	public void joinLobby(String lobbyIdArg) {
		if (usernameNotSet()) return;
		try {
			int lobbyId = Integer.parseInt(lobbyIdArg);
			write(new JoinLobby(username, lobbyId));
		} catch (NumberFormatException e) {
			ui.showError("Invalid number format for argument <id>");
		}
	}

	/**
	 * Checks if the parameters are valid and sends a {@link LobbyCreation} message to the server.
	 * @param numPlayersArg the desired size of the lobby
	 * @param expertModeArg whether to use expert mode in the lobby
	 */
	public void createLobby(String numPlayersArg, String expertModeArg) {
		if (usernameNotSet()) return;
		try {
			int numPlayers = Integer.parseInt(numPlayersArg);
			if (!expertModeArg.equalsIgnoreCase("true") && !expertModeArg.equalsIgnoreCase("false"))
				throw new Exception("Invalid boolean value");
			boolean expertMode = Boolean.parseBoolean(expertModeArg);
			write(new LobbyCreation(username, numPlayers, expertMode));
		} catch (NumberFormatException e) {
			ui.showError("Invalid number format for argument <players>");
		} catch (Exception e) {
			ui.showError("Argument <expert> should be a boolean value (true/false)");
		}
	}

	/**
	 * Saves the reconnection settings.
	 * @param message the message containing the information about the joined lobby
	 */
	public void putReconnectSettings(AcceptedJoinLobby message) {
		int gameId = message.getGameId();
		String passcode = message.getPasscode();
		JsonObject reconnectInfo = new JsonObject();
		reconnectInfo.addProperty("gameId", gameId);
		reconnectInfo.addProperty("passcode", passcode);

		Preferences prefs = Preferences.userRoot();
		prefs.put("reconnect_" + username, new Gson().toJson(reconnectInfo));
	}

	/**
	 * Removes the reconnection settings.
	 */
	public void removeReconnectSettings() {
		Preferences prefs = Preferences.userRoot();
		prefs.remove("reconnect_" + username);
	}

	/**
	 * Checks if a reconnection is available.
	 * @return {@code true} if reconnection settings are found
	 */
	public boolean hasReconnectSettings() {
		return getReconnectSettings() != null;
	}


	private JsonObject getReconnectSettings() {
		Preferences prefs = Preferences.userRoot();
		String prefValue = prefs.get("reconnect_" + username, null);
		return new Gson().fromJson(prefValue, JsonObject.class);
	}

	/**
	 * If the player is in a lobby, sends a {@link LeaveLobby} message to the server.
	 */
	public void leaveLobby() {
		if (gameId == null) {
			ui.showError("Not in a lobby");
			return;
		}
		write(new LeaveLobby(username, gameId));
	}

	private void trySendSetupSelection() {
		if (wizard != null && towerColor != null) {
			write(new GameSetupSelection(username, towerColor, wizard));
		}
	}

	/**
	 * Sends a {@link PlayAssistantCard} message to the server.
	 * @param card the {@link AssistantCard} literal
	 */
	public void playAssistantCard(String card) {
		write(new PlayAssistantCard(username, card));
	}

	/**
	 * Sends a {@link MoveStudent} message to the server.
	 * @param color the {@link Color} literal of the student to move
	 * @param destination where to place the student
	 */
	public void moveStudent(String color, String destination) {
		write(new MoveStudent(username, color, destination));
	}

	/**
	 * Sends a {@link MotherNatureDestination} message to the server.
	 * @param island the identifier of the island where to put Mother Nature
	 */
	public void moveMotherNature(String island) {
		write(new MotherNatureDestination(username, island));
	}

	/**
	 * Sends a {@link SelectCloud} message to the server.
	 * @param cloud the identifier of the selected cloud
	 */
	public void chooseCloud(int cloud) {
		write(new SelectCloud(username, cloud));
	}

	/**
	 * Sets the {@link #characterCard} attribute to save the selected character card while choosing the requested arguments.
	 * @param card the selected card
	 */
	public void setCharacterCard(Integer card) {
		characterCard = card;
	}

	/**
	 * Builds the {@link JsonObject} containing the given arguments to play the selected character card,
	 * sends a {@link PlayCharacterCard} message to the server
	 * and then clears the {@link #characterCard} attribute setting it to {@code null}.
	 * @param sourceColors an array of colors selected as a source for the character card effect
	 * @param destinationColors an array of colors selected as a destination for the character card effect
	 * @param targetColor the single color selected for the character card effect
	 * @param targetIsland the island selected for the character card effect
	 */
	public void playCharacterCard(String[] sourceColors,
								  String[] destinationColors,
								  String targetColor,
								  String targetIsland) {
		JsonObject params = new JsonObject();
		if (sourceColors != null && sourceColors.length > 0) {
			JsonArray src = new JsonArray(sourceColors.length);
			Arrays.stream(sourceColors).forEach(src::add);
			params.add("sourceColors", src);
		}
		if (destinationColors != null && destinationColors.length > 0) {
			JsonArray dst = new JsonArray(destinationColors.length);
			Arrays.stream(destinationColors).forEach(dst::add);
			params.add("destinationColors", dst);
		}
		if (targetColor != null) {
			params.addProperty("targetColor", targetColor);
		}
		if (targetIsland != null) {
			params.addProperty("targetIsland", targetIsland);
		}
		write(new PlayCharacterCard(username, characterCard, new Gson().toJson(params)));
		characterCard = null;
	}
}
