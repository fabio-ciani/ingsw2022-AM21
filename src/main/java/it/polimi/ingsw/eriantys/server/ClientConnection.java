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

public class ClientConnection {
	private final Server server;
	private final Socket socketToClient;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private boolean joinedLobby;
	private Game game;

	public ClientConnection(Server server, Socket socketToClient) throws IOException {
		this.server = server;
		this.socketToClient = socketToClient;
		this.socketToClient.setSoTimeout(5000);
		this.out = new ObjectOutputStream(socketToClient.getOutputStream());
		this.in = new ObjectInputStream(socketToClient.getInputStream());
		this.running = true;
		this.joinedLobby = false;
		this.game = null;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean hasJoinedLobby() {
		return joinedLobby;
	}

	public void setJoinedLobby(boolean joinedLobby) {
		this.joinedLobby = joinedLobby;
	}

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

	public synchronized void write(Message message) {
		try {
			out.reset();
			out.writeObject(message);
		} catch (IOException e) {
			server.disconnect(this);
		}
	}

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
