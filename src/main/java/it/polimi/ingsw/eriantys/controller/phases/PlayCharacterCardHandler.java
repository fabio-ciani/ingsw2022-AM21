package it.polimi.ingsw.eriantys.controller.phases;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayCharacterCard;
import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the action phase message {@link PlayCharacterCard} should be processed.
 */
public abstract class PlayCharacterCardHandler implements MessageHandler {
	protected final Game g;

	public PlayCharacterCardHandler(Game g) {
		this.g = g;
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (g.getInfo().isExpertMode() && m instanceof PlayCharacterCard playCharacterCard)
			process(playCharacterCard);
		else
			g.refuseRequest(m, "Unexpected message");
	}

	@Override
	public String getHelp() {
		return HelpContent.IN_GAME.getContent();
	}

	public void process(PlayCharacterCard message) throws NoConnectionException {
		int card = message.getCharacterCard();
		JsonObject params = message.getParams();

		try {
			g.playCharacterCard(card, params);
		} catch (ItemNotAvailableException e) {
			g.refuseRequest(message, "Item not available");
			return;
		} catch (NoMovementException e) {
			g.refuseRequest(message, "No movement");
			return;
		} catch (InvalidArgumentException e) {
			g.refuseRequest(message, "Invalid argument");
			return;
		} catch (DuplicateNoEntryTileException e) {
			g.refuseRequest(message, "Duplicate no entry tile");
			return;
		}

		g.acceptRequest(message);
	}
}