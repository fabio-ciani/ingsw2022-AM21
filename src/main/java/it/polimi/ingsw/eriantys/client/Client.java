package it.polimi.ingsw.eriantys.client;

import it.polimi.ingsw.eriantys.client.cli.CommandLineInterface;
import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.JoinLobby;
import it.polimi.ingsw.eriantys.messages.client.LobbiesRequest;
import it.polimi.ingsw.eriantys.messages.client.LobbyCreation;
import it.polimi.ingsw.eriantys.messages.server.Accepted;
import it.polimi.ingsw.eriantys.messages.server.AcceptedUsername;
import it.polimi.ingsw.eriantys.messages.server.AvailableLobbies;
import it.polimi.ingsw.eriantys.messages.server.Refused;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Client extends Thread {
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private final UserInterface ui;
	private String username;
	private final GameStatus gameStatus;

	public static void main(String[] args) {
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
		this.out = new ObjectOutputStream(this.socket.getOutputStream());
		this.in = new ObjectInputStream(this.socket.getInputStream());
		this.ui = ui;
		this.gameStatus = new GameStatus();
		this.running = true;
	}

	@Override
	public void run() {
		new Thread(ui::getInputs).start();
		// TODO: 03/05/2022 Reconnect?
		try (socket) {
			while (running) {
				Message message = (Message) in.readObject();
				handleMessage(message);
			}
		} catch (IOException e) {
			// TODO: 03/05/2022 Handle exception
			setRunning(false);
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
			ui.showError("Set a username first");
			return true;
		}
		return false;
	}

	public void handleMessage(Message message) {
		if (message instanceof Accepted) {
			ui.showInfo("Ok");
			if (message instanceof AcceptedUsername m) {
				username = m.getUsername();
			}
		} else if (message instanceof Refused refused) {
			ui.showError(refused.getDetails());
		} else if (message instanceof AvailableLobbies availableLobbies) {
			List<GameInfo> lobbies = availableLobbies.getLobbies();
			if (lobbies.isEmpty()) {
				ui.showInfo("No available lobbies");
			} else {
				for (GameInfo lobby : availableLobbies.getLobbies()) {
					ui.showInfo(lobby.toString());
				}
			}
		} else {
			ui.showInfo("Received " + message.getClass());
		}
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
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

	// ???
	// BoardStatus is the content of BoardUpdate messages
	// public synchronized void updateGameStatus(BoardStatus boardStatus) {}
	// ???
}
