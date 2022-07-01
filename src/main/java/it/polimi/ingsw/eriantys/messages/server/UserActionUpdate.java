package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} sent by the server in order to notify a user's action.
 * The communication item is broadcast through the lobby or game (depending on the application phase).
 * The class represents a non-specific communication item which can be overridden for further object-oriented implementations.
 */
public abstract class UserActionUpdate extends Message {
	private String nextPlayer;

	public UserActionUpdate() {
		super(Server.name);
		this.nextPlayer = null;
	}

	/**
	 * A setter for the username of the next player who is eligible for an action.
	 * @param nextPlayer the username of the player
	 */
	public void setNextPlayer(String nextPlayer) {
		if (this.nextPlayer == null) this.nextPlayer = nextPlayer;
	}

	/**
	 * A getter for the username of the next player who is eligible for an action.
	 * @return the username of the player
	 */
	public String getNextPlayer() {
		return nextPlayer;
	}
}
