package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class JoinLobby extends ConnectionMessage {
	private final int gameId;

	public JoinLobby(String sender, int gameId) {
		super(sender);
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}
}
