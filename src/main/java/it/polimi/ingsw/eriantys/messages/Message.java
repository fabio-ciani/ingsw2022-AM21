package it.polimi.ingsw.eriantys.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private final String sender;

	public Message(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}
}
