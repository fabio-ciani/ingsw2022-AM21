package it.polimi.ingsw.eriantys.messages;

import java.io.Serializable;

/**
 * A class representing the most non-specific communication item exchanged between client-side and server-side.
 */
public abstract class Message implements Serializable {
	private final String sender;

	public Message(String sender) {
		this.sender = sender;
	}

	/**
	 * A getter for the entity which is sending {@link this}.
	 * @return the identifier of the sender
	 */
	public String getSender() {
		return sender;
	}
}
