package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

/**
 * A {@link ConnectionMessage} sent by a client in order to communicate the chosen (global and unique) username
 * and subsequently establish a connection with the server.
 */
public class Handshake extends ConnectionMessage {
	public Handshake(String sender) {
		super(sender);
	}
}
