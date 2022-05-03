package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

public class UserActionUpdate extends Message {
	private String nextPlayer;

	public UserActionUpdate() {
		super(Server.name);
		this.nextPlayer = null;
	}

	public void setNextPlayer(String nextPlayer) {
		if (this.nextPlayer == null) this.nextPlayer = nextPlayer;
	}
}
