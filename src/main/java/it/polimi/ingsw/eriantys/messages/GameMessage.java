package it.polimi.ingsw.eriantys.messages;

/**
 * A class representing a {@link Message} sent while in-game.
 */
public abstract class GameMessage extends Message {
	public GameMessage(String sender) {
		super(sender);
	}
}
