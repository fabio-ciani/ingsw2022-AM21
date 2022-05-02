package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.MotherNatureDestination;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link MotherNatureDestination} should be processed.
 */
public class MotherNatureDestinationHandler implements MessageHandler {
	@Override
	public void handle(GameMessage m) {
		if (m instanceof MotherNatureDestination)
			process((MotherNatureDestination) m);
		else
			// send Refused()
			return;
	}

	private void process(MotherNatureDestination m) {
		// at the end of the method, let Game change state to SelectCloudHandler
	}
}