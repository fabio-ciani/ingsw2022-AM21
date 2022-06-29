package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.model.AssistantCard;

/**
 * A {@link GameMessage} sent by a client in order to inform the server of
 * the {@link AssistantCard} literal chosen by the user in the current planning phase.
 */
public class PlayAssistantCard extends GameMessage {
	private final String assistantCard;

	public PlayAssistantCard(String sender, String assistantCard) {
		super(sender);
		this.assistantCard = assistantCard;
	}

	/**
	 * A getter for the {@link AssistantCard} literal requested by the user.
	 * @return the chosen {@link AssistantCard} literal
	 */
	public String getAssistantCard() {
		return assistantCard;
	}
}
