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

	public MoveStudentHandler(Game g) {
		super(g);
		this.movementCount = 0;

		try {
			this.g.sendBoardUpdate();
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

	private void process(MoveStudent message) throws NoConnectionException {
		String sender = message.getSender();
		String color = message.getColor();
		String destination = message.getDestination();

		try {
			g.moveStudent(sender, color, destination);
		} catch (NoMovementException e) {
			g.refuseRequest(message, "Invalid movement");
			return;
		} catch (IslandNotFoundException e) {
			g.refuseRequest(message, "Island not found: " + destination);
			return;
		}

		g.acceptRequest(message);
		movementCount++;
		checkStateTransition();
	}

	private void checkStateTransition() throws NoConnectionException {
		if (movementCount == g.getCloudSize())	g.receiveMotherNatureMovement();
		else g.sendBoardUpdate();
	}
}