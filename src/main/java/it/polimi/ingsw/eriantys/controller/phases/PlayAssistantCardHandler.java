package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.controller.Game;
import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.client.PlayAssistantCard;
import it.polimi.ingsw.eriantys.messages.server.AssistantCardUpdate;
import it.polimi.ingsw.eriantys.server.HelpContent;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

import java.util.LinkedHashMap;
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

	/**
	 * Constructs a new {@link PlayAssistantCardHandler} for the specified game and notifies the players about the current
	 * state of the game.
	 * @param game the {@link Game} this message handler refers to.
	 */
	public PlayAssistantCardHandler(Game game) {
		this.game = game;
		this.availableCards = game.getAssistantCards();
		this.playedCards = new LinkedHashMap<>();

		try {
			this.game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards), true);
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

	@Override
	public String getHelp() {
		return HelpContent.IN_GAME.getContent();
	}

	@Override
	public void handleDisconnectedUser(String username) throws NoConnectionException {
		List<String> availableCardsForUser = availableCards.get(username);
		String card = null;
		for (int i = 0; i < availableCardsForUser.size() && card == null; i++) {
			String c = availableCardsForUser.get(i);
			if (isPlayable(username, c)) card = c;
		}

		playedCards.put(username, card);
		game.nextPlayer();
		checkStateTransition();
	}

	@Override
	public void sendReconnectUpdate(String username) throws NoConnectionException {
		game.sendInitialBoardStatus();
		game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards), true);
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
		if (playedCards.keySet().size() == game.getInfo().getLobbySize()) {
			game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards), false);
			game.newTurn(playedCards);
		}
		else
			game.sendUpdate(new AssistantCardUpdate(playedCards, availableCards), true);
	}
}