package it.polimi.ingsw.eriantys.messages;

/**
 * A class representing a {@link Message} sent while out-of-game.
 */
public abstract class ConnectionMessage extends Message {
	public ConnectionMessage(String sender) {
		super(sender);
	}
}
