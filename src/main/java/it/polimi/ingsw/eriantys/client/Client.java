package it.polimi.ingsw.eriantys.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.client.cli.CommandLineInterface;
import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.JoinLobby;
import it.polimi.ingsw.eriantys.messages.client.LobbiesRequest;
import it.polimi.ingsw.eriantys.messages.client.LobbyCreation;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.messages.client.*;
import it.polimi.ingsw.eriantys.messages.server.Accepted;
import it.polimi.ingsw.eriantys.messages.server.AcceptedUsername;
import it.polimi.ingsw.eriantys.messages.server.AvailableLobbies;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.model.BoardStatus;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class Client extends Thread {
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private final UserInterface ui;
	private String username;
	private Integer gameId;
	private String passcode; // TODO: 16/05/2022 save on file
	private String towerColor;
	private String wizard;
	private Integer characterCard;
	private BoardStatus boardStatus;
	private List<String> availableCards;

	public static void main(String[] args) throws IOException {
		// TODO: 03/05/2022 Get user interface type from args
		UserInterface ui = new CommandLineInterface();
		// TODO: 03/05/2022 Get server address and port from args
		String serverAddress = "localhost";
		int serverPort = 12345;
		try {
			Client client = new Client(serverAddress, serverPort, ui);
			ui.setClient(client);
			client.start();
		} catch (IOException e) {
			ui.showError("Could not connect to the server");
		}
	}

	public Client(String serverAddress, int serverPort, UserInterface ui) throws IOException {
		this.socket = new Socket(serverAddress, serverPort);
		this.socket.setSoTimeout(5000);
		this.out = new ObjectOutputStream(this.socket.getOutputStream());
		this.in = new ObjectInputStream(this.socket.getInputStream());
		this.ui = ui;
		this.running = true;
	}

	@Override
	public void run() {
		new Thread(ui::getInputs).start();
		// TODO: 03/05/2022 Reconnect?
		try (socket) {
			while (running) {
				Message message = (Message) in.readObject();
				ui.handleMessage(message);
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

	private boolean usernameNotSet() {
		if (username == null) {
			ui.showError("Set a username first using\n /u, /user <username>");
			return true;
		}
		return false;
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

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
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
		if (boardStatus == null) ui.showError("Nothing to show yet");
		return availableCards;
	}

	public void setAvailableCards(List<String> availableCards) {
		this.availableCards = availableCards;
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
}
