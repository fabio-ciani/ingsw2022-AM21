package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link SelectCloud} should be processed.
 */
public class SelectCloudHandler implements MessageHandler {
	@Override
	public void handle(GameMessage m) {
		if (m instanceof SelectCloud)
			process((SelectCloud) m);
		else
			// send Refused()
			return;
	}

	private void process(SelectCloud m) {
		// at the end of the method, let Game change state to SelectCloudHandler
	}
}