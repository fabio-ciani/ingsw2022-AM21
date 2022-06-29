package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.client.Handshake;

/**
 * A message of type {@link Accepted} sent by the server in order to
 * tell the client the acceptance of a {@link Handshake} request.
 */
public class AcceptedUsername extends Accepted {
	private final String username;

	public AcceptedUsername(String username) {
		this.username = username;
	}

	/**
	 * A getter for the (global and unique) username which the client chose.
	 * @return the chosen username
	 */
	public String getUsername() {
		return username;
	}
}
