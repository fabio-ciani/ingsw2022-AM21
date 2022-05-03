package it.polimi.ingsw.eriantys.messages.server;

import java.util.List;
import java.util.Map;

public class AssistantCardUpdate extends UserActionUpdate {
	private final Map<String, String> playedCards;
	private final Map<String, List<String>> availableCards;

	public AssistantCardUpdate(Map<String, String> playedCards, Map<String, List<String>> availableCards) {
		super();
		this.playedCards = playedCards;
		this.availableCards = availableCards;
	}

	public Map<String, String> getPlayedCards() {
		return playedCards;
	}

	public Map<String, List<String>> getAvailableCards() {
		return availableCards;
	}
}
