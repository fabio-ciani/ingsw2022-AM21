package it.polimi.ingsw.eriantys.messages.server;

public class ReconnectionUpdate extends ConnectionUpdate {
	private final boolean gameResumed;
	public ReconnectionUpdate(String subject, int numPlayers, boolean gameResumed) {
		super(subject, numPlayers);
		this.gameResumed = gameResumed;
	}

	public boolean isGameResumed() {
		return gameResumed;
	}
}
