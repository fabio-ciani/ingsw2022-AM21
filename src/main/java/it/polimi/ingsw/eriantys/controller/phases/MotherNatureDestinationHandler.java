package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.MotherNatureDestination;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link MotherNatureDestination} should be processed.
 */
public class MotherNatureDestinationHandler extends PlayCharacterCardHandler {

	/**
	 * Constructs a new {@link MotherNatureDestinationHandler} for the specified game and notifies the players about the
	 * current state of the game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public MotherNatureDestinationHandler(Game game) {
		super(game);

		try {
			this.game.sendBoardUpdate();
		} catch (NoConnectionException e) {
			// TODO handle exception
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof MotherNatureDestination motherNatureDestination)
			process(motherNatureDestination);
		else super.handle(m);
	}

	@Override
	public String getHelp() {
		return super.getHelp();
	}

	@Override
	public void handleDisconnectedUser(String username) throws NoConnectionException {
		game.receiveCloudSelection();
	}

	private void process(MotherNatureDestination message) throws NoConnectionException {
		String destination = message.getDestination();
		boolean gameOver;

		try {
			gameOver = game.moveMotherNature(destination);
		} catch (InvalidArgumentException e) {
			game.refuseRequest(message, "Invalid argument");
			return;
		} catch (IslandNotFoundException e) {
			game.refuseRequest(message, "Island not found: " + destination);
			return;
		}

		game.acceptRequest(message);

		if (gameOver) game.gameOver();
		else game.receiveCloudSelection();
	}
}