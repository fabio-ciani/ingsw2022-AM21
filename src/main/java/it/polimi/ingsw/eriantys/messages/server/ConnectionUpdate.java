package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public abstract class ConnectionUpdate extends Message {
	private final String subject;
	private final int numPlayers;

	public ConnectionUpdate(String subject, int numPlayers) {
		super(Server.name);
		this.subject = subject;
		this.numPlayers = numPlayers;
	}

	public String getSubject() {
		return subject;
	}

	public int getNumPlayers() {
		return numPlayers;
	}
}
