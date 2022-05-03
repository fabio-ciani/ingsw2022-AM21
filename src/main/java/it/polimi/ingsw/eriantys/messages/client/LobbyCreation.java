package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class LobbyCreation extends ConnectionMessage {
	private final int numPlayers;
	private final boolean expertMode;

	public LobbyCreation(String sender, int numPlayers, boolean expertMode) {
		super(sender);
		this.numPlayers = numPlayers;
		this.expertMode = expertMode;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public boolean isExpertMode() {
		return expertMode;
	}
}
