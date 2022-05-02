package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayCharacterCard;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link PlayCharacterCard} should be processed.
 */
public class PlayCharacterCardHandler implements MessageHandler {
	@Override
	public void handle(GameMessage m) {
		if (m instanceof PlayCharacterCard)
			process((PlayCharacterCard) m);
		else
			// send Refused()
			return;
	}

	private void process(PlayCharacterCard m) {
		// at the end of the method, let Game change state to SelectCloudHandler
	}
}