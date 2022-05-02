package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class LobbiesRequest extends ConnectionMessage {
	public LobbiesRequest(String sender) {
		super(sender);
	}
}
