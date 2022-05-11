package it.polimi.ingsw.eriantys.controller.phases;

import com.google.gson.JsonObject;
import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayCharacterCard;
import it.polimi.ingsw.eriantys.messages.server.CharacterCardUpdate;
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
	protected final Game game;
	// TODO: hasPlayedCard flag on Player (only one card can be played by a user in the turn)

	/**
	 * Constructs a new {@link PlayCharacterCardHandler} for the specified game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public PlayCharacterCardHandler(Game game) {
		this.game = game;
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (game.getInfo().isExpertMode() && m instanceof PlayCharacterCard playCharacterCard)
			process(playCharacterCard);
		else
			game.refuseRequest(m, "Unexpected message");
	}

	@Override
	public String getHelp() {
		return HelpContent.IN_GAME.getContent();
	}

	private void process(PlayCharacterCard message) throws NoConnectionException {
		int card = message.getCharacterCard();
		JsonObject params = message.getParams();

		try {
			game.playCharacterCard(card, params);
		} catch (ItemNotAvailableException e) {
			game.refuseRequest(message, "Item not available");
			return;
		} catch (NoMovementException e) {
			game.refuseRequest(message, "No movement");
			return;
		} catch (InvalidArgumentException e) {
			game.refuseRequest(message, "Invalid argument");
			return;
		} catch (DuplicateNoEntryTileException e) {
			game.refuseRequest(message, "Duplicate no entry tile");
			return;
		}

		game.acceptRequest(message);
		game.sendUpdate(new CharacterCardUpdate(card));
	}
}