package it.polimi.ingsw.eriantys.server;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.ConnectionMessage;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.Handshake;
import it.polimi.ingsw.eriantys.messages.client.HelpRequest;
import it.polimi.ingsw.eriantys.messages.server.Refused;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
	private final Server server;
	private final Socket socketToClient;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private boolean running;
	private Game game;

	public ClientConnection(Server server, Socket socketToClient) throws IOException {
		this.server = server;
		this.socketToClient = socketToClient;
		this.out = new ObjectOutputStream(socketToClient.getOutputStream());
		this.in = new ObjectInputStream(socketToClient.getInputStream());
		this.game = null;
	}

	public void setGame(Game game) {
		if (this.game == null) this.game = game;
	}

	public void read() {
		try (socketToClient) {
			running = true;
			while (running) {
				Message message = (Message) in.readObject();
				if (message instanceof Handshake) {
					server.connect(message.getSender(), this);
				} else if (message instanceof ConnectionMessage connectionMessage) {
					server.handleMessage(connectionMessage);
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
				}
			}
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

	public void write(Message message) {
		try (socketToClient) {
			out.writeObject(message);
		} catch (IOException e) {
			server.disconnect(this);
		}
	}
}
