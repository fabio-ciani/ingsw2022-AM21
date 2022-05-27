package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.*;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Server extends Thread {
	private final int port;
	private final ServerSocket serverSocket;
	private boolean running;
	private final Map<Integer, Game> gameById;
	private final Map<String, ClientConnection> connectionByUsername;
	private final Map<String, Boolean> reconnectionSettings;
	private int nextGameId;

	private static final int MIN_NUM_PLAYERS = 2;
	private static final int MAX_NUM_PLAYERS = 3;

	public static final String name = "Server";

	public static void main(String[] args) {
		// TODO: 02/05/2022 Get port number from args
		try {
			Server server = new Server(12345);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server(int port) throws IOException {
		this.port = port;
		this.running = true;
		this.gameById = new HashMap<>();
		this.connectionByUsername = new HashMap<>();
		this.reconnectionSettings = new HashMap<>();
		this.nextGameId = 0;
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		try (serverSocket) {
			System.out.println("Accepting connections on port " + port);
			running = true;
			while (running) {
				Socket socketToClient = serverSocket.accept();
				ClientConnection connection = new ClientConnection(this, socketToClient);
				System.out.println("Client connected at " + socketToClient.getRemoteSocketAddress());
				new Thread(connection::read).start();
				new Thread(connection::ping).start();
			}
		} catch (IOException e) {
			// TODO: 02/05/2022 Handle exception
			throw new RuntimeException(e);
		}
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
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
			reconnectionSettings.putIfAbsent(username, false);
			response = new AcceptedUsername(username);
		}
		connection.write(response);
	}

	public synchronized void reconnect(String username, int gameId, String passcode, ClientConnection connection)
			throws NoConnectionException {
		Message response;
		if (reconnectionSettings.get(username) != null && reconnectionSettings.get(username)) {
			if (!gameById.containsKey(gameId) || gameById.get(gameId) == null)
				response = new RefusedReconnect("Game " + gameId + " does not exist");
			else {
				Game game = gameById.get(gameId);
				if (game.checkCredentials(username, passcode)) {
					response = new Accepted();
					connection.setGame(game);
					game.reconnect(username);
				} else
					response = new RefusedReconnect("Incorrect credentials");
			}
		} else if (!connectionByUsername.containsKey(username)) {
			response = new RefusedReconnect("Username \"" + username + "\" does not exist");
		} else {
			response = new RefusedReconnect("Unable to reconnect to game " + gameId);
		}

		connection.write(response);
	}

	public synchronized void disconnect(ClientConnection connection) {
		connectionByUsername.keySet().stream()
				.filter(k -> connectionByUsername.get(k) == connection)
				.toList()
				.forEach(user -> {
					Game game = connection.getGame();
					connectionByUsername.remove(user);
					if (game != null) {
						reconnectionSettings.put(user, game.isStarted());
						try {
							game.disconnect(user);
						} catch (NoConnectionException e) {
							e.printStackTrace();
						}
						if (game.isEmpty()) gameById.remove(game.getInfo().getGameId());
					}
				});
	}

	public boolean isConnected(String username) {
		return connectionByUsername.get(username) != null;
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

		if (connection.hasJoinedLobby()) {
			System.out.println("Already joined a lobby");
			connection.write(new Refused("Already joined a lobby"));
		}	else if (target == null || target.isStarted()) {
			System.out.printf("Unavailable game: %d%n", gameId);
			connection.write(new Refused("Unavailable game: " + gameId));
		} else {
			String passcode = target.addPlayer(sender);
			if (passcode == null) {
				System.out.printf("Already participating in game: %d%n", gameId);
				connection.write(new Refused("Already participating in game: " + gameId));
			} else {
				System.out.printf("Joined game: %d%n", gameId);
				connection.setGame(target);
				connection.write(new AcceptedJoinLobby(gameId, passcode));
				connection.setJoinedLobby(true);
				reconnectionSettings.put(sender, true);
				target.notifyLobbyChange();
				if (target.meetsStartupCondition())
					target.setup();
			}
		}
	}

	private void handleLeaveLobby(LeaveLobby message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Integer gameId = message.getGameId();
		Game target = gameById.get(gameId);

		if (!connection.hasJoinedLobby()) {
			System.out.println("Not part of a lobby");
			connection.write(new Refused("Not part of a lobby"));
		} else if (target == null || target.isStarted()) {
			System.out.printf("Cannot leave game: %d%n", gameId);
			connection.write(new Refused("Cannot leave game: " + gameId));
		} else if (!target.removePlayer(sender)) {
			System.out.printf("Not participating in game: %d%n", gameId);
			connection.write(new Refused("Not participating in game: " + gameId));
		} else {
			System.out.printf("Left game: %d%n", gameId);
			connection.setGame(null);
			connection.write(new AcceptedLeaveLobby());
			connection.setJoinedLobby(false);
			reconnectionSettings.put(sender, false);
			target.notifyLobbyChange();
			if (target.isEmpty())
				gameById.remove(gameId);
		}
	}

	private void handleLobbyCreation(LobbyCreation message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		int numPlayers = message.getNumPlayers();
		boolean expertMode = message.isExpertMode();

		if (connection.hasJoinedLobby()) {
			System.out.println("Already joined a lobby");
			connection.write(new Refused("Already joined a lobby"));
		} else if (numPlayers < MIN_NUM_PLAYERS || numPlayers > MAX_NUM_PLAYERS) {
			System.out.printf("Invalid number of players: %d%n", numPlayers);
			connection.write(new Refused("Invalid number of players: " + numPlayers));
		} else {
			String username = message.getSender();
			Game game = new Game(this, nextGameId, username, numPlayers, expertMode);
			String passcode = game.addPlayer(username);
			gameById.put(nextGameId, game);
			System.out.printf("Created game successfully: %d%n", nextGameId);
			connection.setGame(game);
			connection.write(new AcceptedJoinLobby(nextGameId, passcode));
			connection.setJoinedLobby(true);
			reconnectionSettings.put(sender, true);
			game.notifyLobbyChange();
			nextGameId++;
		}
	}

	private void handleUnexpectedMessage(ConnectionMessage message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);
		connection.write(new Refused("Unexpected message"));
	}

	public void sendHelp(HelpRequest helpRequest) throws NoConnectionException {
		String sender = helpRequest.getSender();
		ClientConnection connection = getConnection(sender);
		connection.write(new HelpResponse(HelpContent.NO_GAME.getContent()));
	}

	public void gameOver(Game game, List<String> players) {
		for (String player : players) {
			try {
				getConnection(player).setGame(null);
			} catch (NoConnectionException e) {
				System.out.println("this is a printstacktrace");
				e.printStackTrace();
			}
			connectionByUsername.remove(player);
		}
		gameById.remove(game.getInfo().getGameId(), game);
	}
}
