package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} sent by the server in order to communicate the acceptance of the associated client request.
 * The class represents a non-specific communication item which can be overridden for further object-oriented implementations.
 */
public class Accepted extends Message {
	public Accepted() {
		super(Server.name);
	}
}
