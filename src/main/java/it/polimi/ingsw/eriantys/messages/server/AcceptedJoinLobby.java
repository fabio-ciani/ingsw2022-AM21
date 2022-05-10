package it.polimi.ingsw.eriantys.messages.server;

public class AcceptedJoinLobby extends Accepted {
	private final int gameId;
	private final String passcode;

	public AcceptedJoinLobby(int gameId, String passcode) {
		super();
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
