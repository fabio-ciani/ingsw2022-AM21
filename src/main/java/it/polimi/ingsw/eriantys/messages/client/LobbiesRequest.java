package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

/**
 * A {@link ConnectionMessage} sent by a client in order to request a list of the available server-side lobbies.
 */
public class LobbiesRequest extends ConnectionMessage {
	public LobbiesRequest(String sender) {
		super(sender);
	}
}
