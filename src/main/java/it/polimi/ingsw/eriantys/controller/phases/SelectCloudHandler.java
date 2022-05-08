package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.messages.server.BoardUpdate;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link SelectCloud} should be processed.
 */
public class SelectCloudHandler implements MessageHandler {
	private final Game game;

	public SelectCloudHandler(Game game) {
		this.game = game;

		try {
			this.game.sendBoardUpdate();
		} catch (NoConnectionException e) {
			// TODO handle exception
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof SelectCloud selectCloud)
			process(selectCloud);
		else
			game.refuseRequest(m, "Unexpected message");
	}

	private void process(SelectCloud message) throws NoConnectionException {
		String sender = message.getSender();
		int cloud = message.getCloud();

		try {
			game.selectCloud(sender, cloud);
		} catch (InvalidArgumentException e) {
			game.refuseRequest(message, "Nonexistent cloud: " + cloud);
			return;
		} catch (NoMovementException e) {
			game.refuseRequest(message, "Error");
			return;
		}

		game.acceptRequest(message);
		game.advanceTurn();
	}
}