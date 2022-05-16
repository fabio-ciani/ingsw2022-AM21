package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.MoveStudent;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link MoveStudent} should be processed.
 */
public class MoveStudentHandler extends PlayCharacterCardHandler {
	private int movementCount;

	/**
	 * Constructs a new {@link MoveStudentHandler} for the specified game and notifies the players about the current state
	 * of the game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public MoveStudentHandler(Game game) {
		super(game);
		this.movementCount = 0;

		try {
			this.game.sendBoardUpdate();
		} catch (NoConnectionException e) {
			// TODO handle exception
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof MoveStudent moveStudent)
			process(moveStudent);
		else super.handle(m);
	}

	@Override
	public String getHelp() {
		return super.getHelp();
	}

	@Override
	public void handleDisconnectedUser(String username) throws NoConnectionException {
		while (movementCount < 3) {
			// TODO move students at random?
			movementCount++;
			checkStateTransition();
		}
	}

	private void process(MoveStudent message) throws NoConnectionException {
		String sender = message.getSender();
		String color = message.getColor();
		String destination = message.getDestination();

		try {
			game.moveStudent(sender, color, destination);
		} catch (NoMovementException e) {
			game.refuseRequest(message, "Invalid movement");
			return;
		} catch (IslandNotFoundException e) {
			game.refuseRequest(message, "Island not found: " + destination);
			return;
		}

		game.acceptRequest(message);
		movementCount++;
		checkStateTransition();
	}

	private void checkStateTransition() throws NoConnectionException {
		if (movementCount == game.getCloudSize())	game.receiveMotherNatureMovement();
		else game.sendBoardUpdate();
	}
}