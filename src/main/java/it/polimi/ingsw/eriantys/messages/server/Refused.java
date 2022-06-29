package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} sent by the server in order to communicate the refusal of the associated client request.
 * The class represents a non-specific communication item which can be overridden for further object-oriented implementations.
 */
public class Refused extends Message {
	private final String details;

	public Refused(String details) {
		super(Server.name);
		this.details = details;
	}

	/**
	 * A getter for the details attached within the message itself,
	 * which can include a description for the reason behind the refusal.
	 * @return the details of the refusal
	 */
	public String getDetails() {
		return details;
	}
}
