package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

public class PlayAssistantCard extends GameMessage {
	private final String assistantCard;

	public PlayAssistantCard(String sender, String assistantCard) {
		super(sender);
		this.assistantCard = assistantCard;
	}

	public String getAssistantCard() {
		return assistantCard;
	}
}
