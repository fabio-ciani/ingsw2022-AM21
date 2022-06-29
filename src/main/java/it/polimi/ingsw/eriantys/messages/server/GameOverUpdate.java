package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.Board;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to notify the client of the end the game.
 * The condition is met under a number of circumstances based on the players' actions and the status of the {@link Board}.
 */
public class GameOverUpdate extends UserActionUpdate {
	private final String winner;

	public GameOverUpdate(String winner) {
		super();
		this.winner = winner;
	}

	/**
	 * A getter for the winner of the game.
	 * @return the username of the winner, if existent, or a literal representing a tie.
	 */
	public String getWinner() {
		return winner;
	}
}
