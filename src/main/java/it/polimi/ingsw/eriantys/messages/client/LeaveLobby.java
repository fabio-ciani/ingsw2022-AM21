package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class LeaveLobby extends ConnectionMessage {
	public LeaveLobby(String sender) {
		super(sender);
	}
}
