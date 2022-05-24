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
import it.polimi.ingsw.eriantys.model.BoardStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

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

	public static void main(String[] args) throws IOException {
		// TODO: 03/05/2022 Get user interface type from args
		boolean useGui = true;
		// TODO: 03/05/2022 Get server address and port from args
		String serverAddress = "localhost";
		int serverPort = 12345;
		Client client = new Client(serverAddress, serverPort, useGui);
		client.start();
	}

	public Client(String serverAddress, int serverPort, boolean gui) throws IOException {
		try {
			this.ui = gui ? new GraphicalUserInterface() : new CommandLineInterface();
		} catch (IOException e) {
			throw new IOException("Unable to open characters.json", e);
		}
		try {
			this.socket = new Socket(serverAddress, serverPort);
			this.socket.setSoTimeout(5000);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			throw new IOException("Can't connect to the server", e);
		}
		this.running = true;
	}

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
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout");
			e.printStackTrace();
			setRunning(false);
			System.out.println("Stopped");
		} catch (IOException e) {
			// TODO: 03/05/2022 Handle exception
			setRunning(false);
			System.out.println("Stopped");
		} catch (ClassNotFoundException e) {
			// TODO: 03/05/2022 Handle exception?
			throw new RuntimeException(e);
		}
	}

	public void write(Message message) {
		synchronized (out) {
			try {
				out.writeObject(message);
			} catch (IOException e) {
				// TODO: 03/05/2022 Handle exception
				running = false;
			}
		}
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public String getUsername() {
		return username;
	}

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

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getTowerColor() {
		return towerColor;
	}

	public void setTowerColor(String towerColor) {
		this.towerColor = towerColor;
		ui.showInfo(String.format("Tower color set to: %s", this.towerColor));
		trySendSetupSelection();
	}

	public String getWizard() {
		return wizard;
	}

	public void setWizard(String wizard) {
		this.wizard = wizard;
		ui.showInfo(String.format("Wizard set to: %s", this.wizard));
		trySendSetupSelection();
	}

	public BoardStatus getBoardStatus() {
		if (boardStatus == null) ui.showError("Nothing to show yet");
		return boardStatus;
	}

	public void setBoardStatus(BoardStatus boardStatus) {
		this.boardStatus = boardStatus;
	}

	public List<String> getAvailableCards() {
		if (availableCards == null) ui.showError("Nothing to show yet");
		return availableCards;
	}

	public void setAvailableCards(List<String> availableCards) {
		this.availableCards = availableCards;
	}

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
			// TODO: 23/05/2022 lobbies = m.getLobbies(); ?
			ui.handleMessage(m);
		} else if (message instanceof LobbyUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof AssistantCardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof BoardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof CharacterCardUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof UserSelectionUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof GameOverUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof InitialBoardStatus m) {
			ui.handleMessage(m);
		} else if (message instanceof ReconnectionUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof DisconnectionUpdate m) {
			ui.handleMessage(m);
		} else if (message instanceof Ping m) {
			ui.handleMessage(m);
		} else {
			ui.handleMessage(message);
		}
	}

	public void askHelp() {
		if (usernameNotSet()) return;
		write(new HelpRequest(username));
	}

	public void sendHandshake(String username) {
		if(username.matches("\\w+")) {
			write(new Handshake(username));
		} else {
			ui.showError("Invalid username, try again");
		}
	}

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

	public void askLobbies() {
		if (usernameNotSet()) return;
		write(new LobbiesRequest(username));
	}

	public void joinLobby(String lobbyIdArg) {
		if (usernameNotSet()) return;
		try {
			int lobbyId = Integer.parseInt(lobbyIdArg);
			write(new JoinLobby(username, lobbyId));
		} catch (NumberFormatException e) {
			ui.showError("Invalid number format for argument <id>");
		}
	}

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

	public void putReconnectSettings(AcceptedJoinLobby message) {
		int gameId = message.getGameId();
		String passcode = message.getPasscode();
		JsonObject reconnectInfo = new JsonObject();
		reconnectInfo.addProperty("gameId", gameId);
		reconnectInfo.addProperty("passcode", passcode);

		Preferences prefs = Preferences.userRoot();
		prefs.put("reconnect_" + username, new Gson().toJson(reconnectInfo));
	}

	public void removeReconnectSettings() {
		Preferences prefs = Preferences.userRoot();
		prefs.remove("reconnect_" + username);
	}

	public boolean hasReconnectSettings() {
		return getReconnectSettings() != null;
	}

	private JsonObject getReconnectSettings() {
		Preferences prefs = Preferences.userRoot();
		String prefValue = prefs.get("reconnect_" + username, null);
		return new Gson().fromJson(prefValue, JsonObject.class);
	}

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

	public void playAssistantCard(String card) {
		write(new PlayAssistantCard(username, card));
	}

	public void moveStudent(String color, String destination) {
		write(new MoveStudent(username, color, destination));
	}

	public void moveMotherNature(String island) {
		write(new MotherNatureDestination(username, island));
	}

	public void chooseCloud(int cloud) {
		write(new SelectCloud(username, cloud));
	}

	public void selectCharacterCard(int card) {
		characterCard = card;
	}

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
