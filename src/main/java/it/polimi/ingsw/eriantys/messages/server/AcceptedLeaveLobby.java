package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.client.LeaveLobby;

/**
 * A message of type {@link Accepted} sent by the server in order to
 * tell the client the acceptance of a {@link LeaveLobby} request.
 */
public class AcceptedLeaveLobby extends Accepted {
	public AcceptedLeaveLobby() {
		super();
	}
}
