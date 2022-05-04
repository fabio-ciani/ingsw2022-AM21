package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.*;
import it.polimi.ingsw.eriantys.messages.server.Accepted;
import it.polimi.ingsw.eriantys.messages.server.AvailableLobbies;
import it.polimi.ingsw.eriantys.messages.server.HelpResponse;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Server extends Thread {
	private final int port;
	private ServerSocket serverSocket;
	private boolean running;
	private final Map<Integer, Game> gameById;
	private final Map<String, ClientConnection> connectionByUsername;
	private int nextGameId;

	public static final String name = "Server";

	public static void main(String[] args) {
		// TODO: 02/05/2022 Get port number from args
		Server server = new Server(12345);
		server.start();
	}

	public Server(int port) {
		this.port = port;
		this.gameById = new HashMap<>();
		this.connectionByUsername = new HashMap<>();
		this.nextGameId = 0;
	}

	@Override
	public void run() {
		try (ServerSocket socket = new ServerSocket(port)) {
			System.out.println("Accepting connections on port " + port);
			running = true;
			while (running) {
				Socket socketToClient = serverSocket.accept();
				ClientConnection connection = new ClientConnection(this, socketToClient);
				System.out.println("Client connected at " + socketToClient.getRemoteSocketAddress());
				new Thread(connection::read).start();
			}
		} catch (IOException e) {
			// TODO: 02/05/2022 Handle exception
			throw new RuntimeException(e);
		}
	}

	public synchronized void connect(String username, ClientConnection connection) {
		Message response;
		if (connectionByUsername.containsKey(username)) {
			response = new Refused("Username \"" + username + "\" already exists");
		} else if (connectionByUsername.containsValue(connection)) {
			Optional<String> existingUsername = connectionByUsername.keySet().stream()
					.filter(k -> connectionByUsername.get(k) == connection)
					.reduce((a, b) -> a);
			String details = existingUsername
					.map(u -> "Client already connected with username \"" + u + "\"")
					.orElse("Client already connected but no username found");
			response = new Refused(details);
		} else {
			connectionByUsername.put(username, connection);
			response = new Accepted();
		}
		connection.write(response);
	}

	public synchronized void disconnect(ClientConnection connection) {
		connectionByUsername.keySet().stream()
				.filter(k -> connectionByUsername.get(k) == connection)
				.toList()
				.forEach(connectionByUsername::remove);
	}

	public ClientConnection getConnection(String username) throws NoConnectionException {
		ClientConnection connection = connectionByUsername.get(username);
		if (connection == null) throw new NoConnectionException();
		return connection;
	}

	public void handleMessage(ConnectionMessage message) throws NoConnectionException {
		if (message instanceof LobbiesRequest lobbiesRequest)
			handleLobbiesRequest(lobbiesRequest);
		else if (message instanceof JoinLobby joinLobby)
			handleJoinLobby(joinLobby);
		else if (message instanceof LeaveLobby leaveLobby)
			handleLeaveLobby(leaveLobby);
		else if (message instanceof LobbyCreation lobbyCreation)
			handleLobbyCreation(lobbyCreation);
		else
			handleUnexpectedMessage(message);
	}

	private void handleLobbiesRequest(LobbiesRequest message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Message response = new AvailableLobbies(
				gameById.values().stream().filter(g -> !g.isStarted()).map(Game::getInfo).toList());
		System.out.println("Sending lobby list...");
		connection.write(response);
	}

	private void handleJoinLobby(JoinLobby message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Integer gameId = message.getGameId();
		Game target = gameById.get(gameId);

		if (target == null || target.isStarted()) {
			System.out.printf("Unavailable game: %d%n", gameId);
			connection.write(new Refused("Unavailable game: " + gameId));
		} else if (!target.addPlayer(sender)) {
			System.out.printf("Already participating in game: %d%n", gameId);
			connection.write(new Refused("Already participating in game: " + gameId));
		} else {
			System.out.printf("Joined game: %d%n", gameId);
			connection.write(new Accepted());
			target.notifyLobbyChange();
			if (target.meetsStartupCondition())
				target.setup();
		}
	}

	private void handleLeaveLobby(LeaveLobby message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Integer gameId = message.getGameId();
		Game target = gameById.get(gameId);

		if (target == null || target.isStarted()) {
			System.out.printf("Cannot leave game: %d%n", gameId);
			connection.write(new Refused("Cannot leave game: " + gameId));
		} else if (!target.removePlayer(sender)) {
			System.out.printf("Not participating in game: %d%n", gameId);
			connection.write(new Refused("Not participating in game: " + gameId));
		} else {
			System.out.printf("Left game: %d%n", gameId);
			connection.write(new Accepted());
			target.notifyLobbyChange();
		}
	}

	private void handleLobbyCreation(LobbyCreation message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		int numPlayers = message.getNumPlayers();
		boolean expertMode = message.isExpertMode();

		if (numPlayers < 2 || numPlayers > 4) {
			System.out.printf("Invalid number of players: %d%n", numPlayers);
			connection.write(new Refused("Invalid number of players: " + numPlayers));
		} else {
			String username = message.getSender();
			Game game = new Game(this, nextGameId, username, numPlayers, expertMode);
			game.addPlayer(username);
			gameById.put(nextGameId, game);
			nextGameId++;
			System.out.printf("Created game successfully: %d%n", nextGameId);
			connection.write(new Accepted());
			game.notifyLobbyChange();
		}
	}

	private void handleUnexpectedMessage(ConnectionMessage message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);
		connection.write(new Refused("Unexpected message"));
	}

	public void sendHelp(HelpRequest helpRequest) throws NoConnectionException {
		Message response = new HelpResponse(HelpContents.OUT_OF_LOBBY.getContent());
		ClientConnection connection = getConnection(helpRequest.getSender());
		connection.write(response);
	}
}
