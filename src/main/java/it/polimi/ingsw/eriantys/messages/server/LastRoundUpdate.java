package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.Board;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to notify the client of the last round of the game.
 * The condition is met under a number of circumstances based on the players' actions and the status of the {@link Board}.
 */
public class LastRoundUpdate extends UserActionUpdate {
	public LastRoundUpdate() {
		super();
	}
}
