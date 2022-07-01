package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.*;
import it.polimi.ingsw.eriantys.messages.server.*;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class represents the application server thread. It handles all the clients, instantiating
 * {@link ClientConnection} objects for each of them, and exposes various methods
 */
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

	/**
	 * The method is called to launch the server-side application.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 9133;
		Options options = new Options();
		options.addOption(new Option("p", "port", true, "Server port"));
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("p")) {
				int port = Integer.parseInt(line.getOptionValue("port"));
				if (port >= 0 && port <= 65535)
					serverPort = port;
			}
		}
		catch (ParseException e) {
			System.out.println("Parsing failed");
			System.exit(1);
		}
		try {
			Server server = new Server(serverPort);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a {@code Server} object, starting the {@link ServerSocket}.
	 * @param port the port number on which the {@code Server} will run
	 * @throws IOException if the {@link ServerSocket} could not be opened
	 */
	public Server(int port) throws IOException {
		this.port = port;
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
			System.out.println("The server has stopped due to the following exception:");
			e.printStackTrace();
		}
	}

	/**
	 * Handles a user connection by processing a {@link Handshake} communication item.
	 * @param username the username requested by the client
	 * @param connection a reference to the client connection instance
	 */
	public synchronized void connect(String username, ClientConnection connection) {
		Message response;
		if (connectionByUsername.containsKey(username)) {
			response = new Refused("The requested username already exists");
		} else if (connectionByUsername.containsValue(connection)) {
			Optional<String> existingUsername = connectionByUsername.keySet().stream()
					.filter(k -> connectionByUsername.get(k) == connection)
					.reduce((a, b) -> a);
			String details = existingUsername
					.map(u -> "Client already connected with username " + u)
					.orElse("Client already connected, but no username found");
			response = new Refused(details);
		} else {
			connectionByUsername.put(username, connection);
			reconnectionSettings.putIfAbsent(username, false);
			response = new AcceptedUsername(username);
		}
		connection.write(response);
	}

	/**
	 * Handles a user reconnection by processing a {@link Reconnect} communication item.
	 * @param username the username which has been chosen by the client prior to its disconnection
	 * @param gameId the identifier of the game inside which the client was playing prior to its disconnection
	 * @param passcode the hexadecimal code which has been associated with the client by the disconnection handling infrastructure
	 * @param connection a reference to the client connection instance
	 */
	public synchronized void reconnect(String username, int gameId, String passcode, ClientConnection connection) {
		Message response;
		if (reconnectionSettings.get(username) != null && reconnectionSettings.get(username)) {
			if (!gameById.containsKey(gameId) || gameById.get(gameId) == null)
				response = new RefusedReconnect("The game #" + gameId + " does not exist");
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
			response = new RefusedReconnect("The requested username does not exist");
		} else {
			response = new RefusedReconnect("Unable to reconnect to game #" + gameId);
		}

		connection.write(response);
	}

	/**
	 * Handles a user disconnection, causing an update on the internal state of the class.
	 * @param connection a reference to the client connection instance
	 */
	public synchronized void disconnect(ClientConnection connection) {
		connection.setRunning(false);
		connectionByUsername.keySet().stream()
				.filter(k -> connectionByUsername.get(k) == connection)
				.toList()
				.forEach(user -> {
					Game game = connection.getGame();
					connectionByUsername.remove(user);
					if (game != null) {
						reconnectionSettings.put(user, game.isStarted());
						game.disconnect(user);
						if (game.isEmpty()) gameById.remove(game.getInfo().getGameId());
					}
				});
	}

	/**
	 * A method to know if a user with a specified identifier exists or not.
	 * @param username the target user
	 * @return {@code true} if and only if a {@link ClientConnection} exists for the target username
	 */
	public boolean isConnected(String username) {
		return connectionByUsername.get(username) != null;
	}

	/**
	 * A getter for the client connection instance of a user.
	 * @param username the target user
	 * @return a reference to the client connection
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	public ClientConnection getConnection(String username) throws NoConnectionException {
		ClientConnection connection = connectionByUsername.get(username);
		if (connection == null) throw new NoConnectionException();
		return connection;
	}

	/**
	 * Handles a user request by processing a {@link ConnectionMessage} communication item.
	 * @param message the target message to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
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

	/**
	 * Handles a user request by processing a {@link LobbiesRequest} communication item.
	 * @param message the target message to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	private void handleLobbiesRequest(LobbiesRequest message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Message response = new AvailableLobbies(
				gameById.values().stream().filter(g -> !g.isStarted()).map(Game::getInfo).toList());
		System.out.println("Sending lobby list...");
		connection.write(response);
	}

	/**
	 * Handles a user request by processing a {@link JoinLobby} communication item.
	 * @param message the target message to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	private void handleJoinLobby(JoinLobby message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Integer gameId = message.getGameId();
		Game target = gameById.get(gameId);

		if (connection.hasJoinedLobby()) {
			System.out.println("Already joined a lobby");
			connection.write(new Refused("Already joined a lobby"));
		}	else if (target == null || target.isStarted()) {
			System.out.printf("Unavailable game: #%d%n", gameId);
			connection.write(new Refused("Unavailable game: #" + gameId));
		} else {
			String passcode = target.addPlayer(sender);
			if (passcode == null) {
				System.out.printf("Already participating in game: #%d%n", gameId);
				connection.write(new Refused("Already participating in game: #" + gameId));
			} else {
				System.out.printf("Joined game: #%d%n", gameId);
				connection.setGame(target);
				connection.write(new AcceptedJoinLobby(gameId, passcode));
				connection.setJoinedLobby(true);
				reconnectionSettings.put(sender, true);
				target.notifyLobbyChange();
				if (target.meetsStartupCondition()) {
					target.setup();
					target.promptSelection();
				}
			}
		}
	}

	/**
	 * Handles a user request by processing a {@link LeaveLobby} communication item.
	 * @param message the target message to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	private void handleLeaveLobby(LeaveLobby message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);

		Integer gameId = message.getGameId();
		Game target = gameById.get(gameId);

		if (!connection.hasJoinedLobby()) {
			System.out.println("Not part of a lobby");
			connection.write(new Refused("Not part of a lobby"));
		} else if (target == null || target.isStarted()) {
			System.out.printf("Cannot leave game: #%d%n", gameId);
			connection.write(new Refused("Cannot leave game: #" + gameId));
		} else if (!target.removePlayer(sender)) {
			System.out.printf("Not participating in game: #%d%n", gameId);
			connection.write(new Refused("Not participating in game: #" + gameId));
		} else {
			System.out.printf("Left game: #%d%n", gameId);
			connection.setGame(null);
			connection.write(new AcceptedLeaveLobby());
			connection.setJoinedLobby(false);
			reconnectionSettings.put(sender, false);
			target.notifyLobbyChange();
			if (target.isEmpty())
				gameById.remove(gameId);
		}
	}

	/**
	 * Handles a user request by processing a {@link LobbyCreation} communication item.
	 * @param message the target message to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
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
			System.out.printf("Game created successfully: #%d%n", nextGameId);
			connection.setGame(game);
			connection.write(new AcceptedJoinLobby(nextGameId, passcode));
			connection.setJoinedLobby(true);
			reconnectionSettings.put(sender, true);
			game.notifyLobbyChange();
			nextGameId++;
		}
	}

	/**
	 * Handles the processing of an unexpected {@link ConnectionMessage} communication item.
	 * @param message the target message
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	private void handleUnexpectedMessage(ConnectionMessage message) throws NoConnectionException {
		String sender = message.getSender();
		ClientConnection connection = getConnection(sender);
		connection.write(new Refused("Unexpected message"));
	}

	/**
	 * Handles the processing of a {@link HelpRequest} communication item.
	 * @param helpRequest the target request to process
	 * @throws NoConnectionException if no connection can be retrieved for the target player
	 */
	public void sendHelp(HelpRequest helpRequest) throws NoConnectionException {
		String sender = helpRequest.getSender();
		ClientConnection connection = getConnection(sender);
		connection.write(new HelpResponse(HelpContent.NO_GAME.getContent()));
	}

	/**
	 * Processes the server-side end of the game.
	 * @param game the identifier of the game which has been ended
	 * @param players the list of usernames who played the game
	 */
	public void gameOver(Game game, List<String> players) {
		for (String player : players) {
			try {
				Game playerGame = getConnection(player).getGame();
				if (playerGame != null && playerGame.getInfo().getGameId() == game.getInfo().getGameId()) {
					getConnection(player).setGame(null);
					connectionByUsername.remove(player);
				}
			} catch (NoConnectionException e) {
				System.out.println("This is a Throwable#printStackTrace() method call.");
				e.printStackTrace();
				connectionByUsername.remove(player);
			}
		}
		gameById.remove(game.getInfo().getGameId(), game);
	}
}
