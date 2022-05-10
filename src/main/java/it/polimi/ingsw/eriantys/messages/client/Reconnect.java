package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

public class Reconnect extends ConnectionMessage {
	private final int gameId;
	private final String passcode;

	public Reconnect(String sender, int gameId, String passcode) {
		super(sender);
		this.gameId = gameId;
		this.passcode = passcode;
	}

	public int getGameId() {
		return gameId;
	}

	public String getPasscode() {
		return passcode;
	}
}
