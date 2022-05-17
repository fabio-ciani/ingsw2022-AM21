package it.polimi.ingsw.eriantys.messages.server;

public class DisconnectionUpdate extends ConnectionUpdate {
	private final boolean gameIdle;

	public DisconnectionUpdate(String subject, int numPlayers, boolean gameIdle) {
		super(subject, numPlayers);
		this.gameIdle = gameIdle;
	}

	public boolean isGameIdle() {
		return gameIdle;
	}
}
