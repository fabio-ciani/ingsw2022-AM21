package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public class Refused extends Message {
	private final String details;

	public Refused(String details) {
		super(Server.name);
		this.details = details;
	}

	public String getDetails() {
		return details;
	}
}
