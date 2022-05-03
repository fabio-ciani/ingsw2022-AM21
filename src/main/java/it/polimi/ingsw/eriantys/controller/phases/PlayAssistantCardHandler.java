package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayAssistantCard;
import it.polimi.ingsw.eriantys.messages.server.AssistantCardUpdate;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This concrete implementation for the state design pattern involving {@link MessageHandler}
 * defines how the planning phase message {@link PlayAssistantCard} should be processed.
 */
public class PlayAssistantCardHandler implements MessageHandler {
	private final Game game;
	private final Map<String, List<String>> availableCards;
	private final Map<String, String> playedCards;

	public PlayAssistantCardHandler(Game game) {
		this.game = game;
		this.availableCards = game.getAssistantCards();
		this.playedCards = new HashMap<>();

		try {
			this.game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards));
		} catch (NoConnectionException e) {
			// TODO handle exception
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(GameMessage m) throws NoConnectionException {
		if (m instanceof PlayAssistantCard playAssistantCard)
			process(playAssistantCard);
		else
			game.refuseRequest(m, "Unexpected message");
	}

	private void process(PlayAssistantCard message) throws NoConnectionException {
		String sender = message.getSender();
		String card = message.getAssistantCard();

		if (playedCards.containsKey(sender))
			game.refuseRequest(message, "Already played assistant card");
		else if (!isPlayable(sender, card))
			game.refuseRequest(message, "Cannot play card: " + card);
		else {
			playedCards.put(sender, card);
			game.acceptRequest(message);
			game.nextPlayer();
			checkStateTransition();
		}
	}

	private boolean isPlayable(String username, String card) {
		List<String> available = availableCards.get(username);
		if (!available.contains(card))
			return false;
		if (!playedCards.containsValue(card))
			return true;
		return available.stream().filter(c -> !playedCards.containsValue(c)).toList().size() == 0;
	}

	private void checkStateTransition() throws NoConnectionException {
		if (playedCards.keySet().size() == game.getInfo().getLobbySize())
			game.newTurn(playedCards);
		else
			game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards));
	}
}