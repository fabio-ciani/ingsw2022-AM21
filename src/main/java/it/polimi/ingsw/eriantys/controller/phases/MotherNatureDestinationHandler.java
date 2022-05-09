package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.MotherNatureDestination;
import it.polimi.ingsw.eriantys.messages.server.GameOverUpdate;
import it.polimi.ingsw.eriantys.model.exceptions.*;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link MotherNatureDestination} should be processed.
 */
public class MotherNatureDestinationHandler extends PlayCharacterCardHandler {
	public MotherNatureDestinationHandler(Game g) {
		super(g);

		try {
			this.g.sendBoardUpdate();
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

	private void process(MotherNatureDestination message) throws NoConnectionException {
		String destination = message.getDestination();
		String winner;

		try {
			winner = g.moveMotherNature(destination);
		} catch (InvalidArgumentException e) {
			g.refuseRequest(message, "Invalid argument");
			return;
		} catch (IslandNotFoundException e) {
			g.refuseRequest(message, "Island not found: " + destination);
			return;
		}

		g.acceptRequest(message);

		if (winner != null && !winner.isEmpty())
			g.sendUpdate(new GameOverUpdate(winner));
		else
			g.receiveCloudSelection();
	}
}