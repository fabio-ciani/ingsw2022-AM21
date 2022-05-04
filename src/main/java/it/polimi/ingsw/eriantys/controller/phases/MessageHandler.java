package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * The processing of client-side commands by the controller is operated by a message handler entity.
 * This abstract class encloses a state pattern, which will be implemented by concrete classes.
 */
public interface MessageHandler {
	void handle(GameMessage m) throws NoConnectionException;

	// FIXME: 04/05/2022 Implement method in all classes and remove default
	default String getHelp() {
		return null;
	}
}