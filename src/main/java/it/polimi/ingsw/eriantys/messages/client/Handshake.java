package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class Handshake extends ConnectionMessage {
	public Handshake(String sender) {
		super(sender);
	}
}
