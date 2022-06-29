package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.client.Reconnect;

/**
 * A message of type {@link Refused} sent by the server in order to
 * tell the client the acceptance of a {@link Reconnect} request.
 */
public class RefusedReconnect extends Refused {
	public RefusedReconnect(String s) {
		super(s);
	}
}
