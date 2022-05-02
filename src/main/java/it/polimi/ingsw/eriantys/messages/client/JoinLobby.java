package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class JoinLobby extends ConnectionMessage {
	public JoinLobby(String sender) {
		super(sender);
	}
}
