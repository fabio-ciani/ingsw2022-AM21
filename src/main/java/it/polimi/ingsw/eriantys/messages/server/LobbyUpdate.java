package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public class LobbyUpdate extends Message {
	public LobbyUpdate() {
		super(Server.name);
	}
}
