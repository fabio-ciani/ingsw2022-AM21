package it.polimi.ingsw.eriantys.messages;

import it.polimi.ingsw.eriantys.server.Server;

public class Ping extends Message {
	public Ping() {
		super(Server.name);
	}

	public Ping(String sender) {
		super(sender);
	}
}