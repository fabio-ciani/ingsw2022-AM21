package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.MoveStudent;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link MoveStudent} should be processed.
 */
public class MoveStudentHandler implements MessageHandler {
	private int count;

	@Override
	public void handle(GameMessage m) {
		if (m instanceof MoveStudent)
			process((MoveStudent) m);
		else
			// send Refused()
			return;
	}

	private void process(MoveStudent m) {
		// if the user who is playing the turn (curr) has sent a MoveStudent message, then increment count
		// if count == 3, then change state to MotherNatureDestination
	}
}