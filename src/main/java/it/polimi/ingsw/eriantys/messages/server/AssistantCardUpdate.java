package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.model.AssistantCard;

import java.util.List;
import java.util.Map;

/**
 * A message of type {@link UserActionUpdate} sent by the server in order to
 * notify the client of the available {@link AssistantCard} literals in the turn.
 * The communication item carries, inside its structure,
 * a mapping between usernames and previously selected literals during the current round.
 */
public class AssistantCardUpdate extends UserActionUpdate {
	private final Map<String, String> playedCards;
	private final Map<String, List<String>> availableCards;

	public AssistantCardUpdate(Map<String, String> playedCards, Map<String, List<String>> availableCards) {
		super();
		this.playedCards = playedCards;
		this.availableCards = availableCards;
	}

	/**
	 * A getter for the mapping between usernames and previously selected {@link AssistantCard} literals.
	 * @return the internal representation of the mapping
	 */
	public Map<String, String> getPlayedCards() {
		return playedCards;
	}

	/**
	 * A getter for the available {@link AssistantCard} literals in the turn.
	 * @return the available literals
	 */
	public Map<String, List<String>> getAvailableCards() {
		return availableCards;
	}
}
