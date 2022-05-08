package it.polimi.ingsw.eriantys.messages.server;

public class GameOverUpdate extends UserActionUpdate {
	private final String winner;

	public GameOverUpdate(String winner) {
		super();
		this.winner = winner;
	}

	public String getWinner() {
		return winner;
	}
}
