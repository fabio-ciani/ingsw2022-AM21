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
	 * Constructs a new {@link MotherNatureDestinationHandler} for the specified game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public MotherNatureDestinationHandler(Game game) {
		super(game);
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

	@Override
	public void sendReconnectUpdate(String username) throws NoConnectionException {
		game.sendBoardUpdate();
	}

	private void process(MotherNatureDestination message) throws NoConnectionException {
		String destination = message.getDestination();
		boolean gameOver;

		try {
			gameOver = game.moveMotherNature(destination);
		} catch (InvalidArgumentException e) {
			game.refuseRequest(message, e.getMessage() != null ? e.getMessage() : "Invalid argument");
			return;
		} catch (IslandNotFoundException e) {
			game.refuseRequest(message, "Island not found: " + destination);
			return;
		} catch (NotEnoughMovementsException e) {
			game.refuseRequest(message, "Not enough movements: " + e.getMessage());
			return;
		}

		game.acceptRequest(message);

		if (gameOver) game.gameOver();
		else game.receiveCloudSelection();
	}
}