package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A class representing a {@link Message} sent after an event regarding disconnection or reconnection scenarios.
 */
public abstract class ConnectionUpdate extends Message {
	private final String subject;
	private final int numPlayers;

	public ConnectionUpdate(String subject, int numPlayers) {
		super(Server.name);
		this.subject = subject;
		this.numPlayers = numPlayers;
	}

	/**
	 * A getter for the user which caused the event.
	 * @return the subject's username
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * A getter for the updated number of players after the event (in the lobby or game, depending on the application phase).
	 * @return the number of players
	 */
	public int getNumPlayers() {
		return numPlayers;
	}
}
