package it.polimi.ingsw.eriantys.messages;

import it.polimi.ingsw.eriantys.server.Server;

/**
 * A {@link Message} which acts to implement the disconnection handling behaviour.
 * This communication item lets the other end of the channel know that transmission is still happening.
 */
public class Ping extends Message {
	public Ping() {
		super(Server.name);
	}

	public Ping(String sender) {
		super(sender);
	}
}