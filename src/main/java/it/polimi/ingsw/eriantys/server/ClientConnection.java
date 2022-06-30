package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.messages.client.LeaveLobby;
import it.polimi.ingsw.eriantys.messages.client.Reconnect;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * This class represents a single client-server connection.
 * It exposes methods which allow to read from and write to the connection's I/O streams.
 */
public class ClientConnection {
	private final Server server;
	private final Socket socketToClient;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private boolean joinedLobby;
	private Game game;

	/**
	 * Constructs a new instance of {@link ClientConnection} with the specified parameters.
	 * @param server the game server
	 * @param socketToClient the socket between the server and this connection's client
	 * @throws IOException if an error occurs when retrieving the input or output stream
	 */
	public ClientConnection(Server server, Socket socketToClient) throws IOException {
		this.server = server;
		this.socketToClient = socketToClient;
		this.socketToClient.setSoTimeout(10000);
		this.out = new ObjectOutputStream(socketToClient.getOutputStream());
		this.in = new ObjectInputStream(socketToClient.getInputStream());
		this.running = true;
		this.joinedLobby = false;
		this.game = null;
	}

	/**
	 * Sets the {@code running} member variable to the specified value.
	 * @param running the desired value
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * A getter for the game which {@code this} refers to.
	 * @return the game which the connection refers to (if there is one), or {@code null} otherwise.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Sets the connection's game reference to the specified {@link Game}.
	 * @param game the connection's new {@link Game} reference
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * A getter to know if the client is inside a game lobby.
	 * @return {@code true} if and only if the client associated with {@code this} has joined a game lobby
	 */
	public boolean hasJoinedLobby() {
		return joinedLobby;
	}

	/**
	 * Sets the {@code joinedLobby} member variable to the specified value.
	 * @param joinedLobby the desired value
	 */
	public void setJoinedLobby(boolean joinedLobby) {
		this.joinedLobby = joinedLobby;
	}

	/**
	 * Continuously checks for new messages being sent by the client through the connection socket's input stream and
	 * handles them according to the game phase, disconnecting the client if an I/O error occurs
	 * or if the client is unresponsive to ping messages.
	 */
	public void read() {
		try (socketToClient) {
			while (running) { // TODO: try {} catch(NoConnectionException e) {}
				Message message = (Message) in.readObject();
				if (message instanceof Reconnect reconnect) {
					String sender = reconnect.getSender();
					int gameId = reconnect.getGameId();
					String passcode = reconnect.getPasscode();
					server.reconnect(sender, gameId, passcode, this);
				} else if (message instanceof Handshake) {
					server.connect(message.getSender(), this);
				} else if (message instanceof ConnectionMessage connectionMessage) {
					if (game != null && !(message instanceof LeaveLobby)) {
						Message response = new Refused("Already participating in a game");
						write(response);
					} else {
						server.handleMessage(connectionMessage);
					}
				} else if (message instanceof GameMessage gameMessage) {
					if (game == null) {
						Message response = new Refused("Not in a lobby yet");
						write(response);
					} else {
						game.handleMessage(gameMessage);
					}
				} else if (message instanceof HelpRequest helpRequest) {
					if (game == null) {
						server.sendHelp(helpRequest);
					} else {
						game.sendHelp(helpRequest);
					}
				} /*else if (message instanceof Ping) {
					System.out.println("Ping - " + socketToClient.getPort());
				}*/
			}
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout");
			server.disconnect(this);
		} catch (IOException e) {
			server.disconnect(this);
		} catch (NoConnectionException e) {
			// TODO handle exception?
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO: 02/05/2022 Handle exception?
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes the specified {@link Message} to the connection socket's output stream,
	 * disconnecting the client if an I/O error occurs.
	 * @param message the message to be written and sent to the client
	 */
	public synchronized void write(Message message) {
		try {
			out.reset();
			out.writeObject(message);
		} catch (IOException e) {
			server.disconnect(this);
		}
	}

	/**
	 * Sends a {@link Ping} message to the client approximately every 2.5 seconds in order to
	 * ensure that the connection is working, disconnecting the client if an I/O error occurs.
	 */
	public void ping() {
		try {
			while (running) {
				write(new Ping());
				Thread.sleep(2500);
			}
		} catch (InterruptedException e) {
			server.disconnect(this);
		}
	}
}
