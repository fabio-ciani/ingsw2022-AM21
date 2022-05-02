package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public class Accepted extends Message {
	public Accepted() {
		super(Server.name);
	}
}
