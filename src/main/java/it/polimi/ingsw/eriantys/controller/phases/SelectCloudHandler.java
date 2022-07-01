package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.SelectCloud;
import it.polimi.ingsw.eriantys.messages.server.AssistantCardUpdate;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.LinkedHashMap;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link SelectCloud} should be processed.
 */
public class SelectCloudHandler extends PlayCharacterCardHandler {

	/**
	 * Constructs a new {@link SelectCloudHandler} for the specified game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public SelectCloudHandler(Game game) {
		super(game);
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof SelectCloud selectCloud)
			process(selectCloud);
		else
			super.handle(m);
	}

	@Override
	public String getHelp() {
		return HelpContent.IN_GAME.getContent();
	}

	@Override
	public void handleDisconnectedUser(String username) {
		game.advanceTurn();
	}

	@Override
	public void sendReconnectUpdate(String username) {
		game.sendBoardUpdate();
		game.sendUpdate(new AssistantCardUpdate(new LinkedHashMap<>(), game.getAssistantCards()), false);
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
		game.sendBoardUpdate();
		game.advanceTurn();
	}
}