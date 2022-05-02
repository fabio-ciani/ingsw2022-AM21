package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.server.Accepted;
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

	public void handleMessage(ConnectionMessage message) {

	}
}
