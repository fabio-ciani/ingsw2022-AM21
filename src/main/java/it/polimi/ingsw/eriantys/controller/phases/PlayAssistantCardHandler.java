package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayAssistantCard;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the planning phase message {@link PlayAssistantCard} should be processed.
 */
public class PlayAssistantCardHandler implements MessageHandler {
	private boolean transition;

	@Override
	public void handle(GameMessage m) {
		if (m instanceof PlayAssistantCard)
			process((PlayAssistantCard) m);
		else
			// send Refused()
			return;
	}

	public void setTransition() {
		transition = true;
	}

	private void process(PlayAssistantCard m) {
		// if every player has set an assistant card, then Game (?) must set transition to true
		// if transition is true, then change state to MoveStudent
	}
}