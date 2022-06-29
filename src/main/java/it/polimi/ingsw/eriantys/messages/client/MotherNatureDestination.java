package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.model.IslandGroup;

/**
 * A {@link GameMessage} sent by a client in order to inform the server of
 * the {@link IslandGroup} literal for the Mother Nature pawn movement specified by the user in the current action phase.
 */
public class MotherNatureDestination extends GameMessage {
	private final String destination;

	public MotherNatureDestination(String sender, String destination) {
		super(sender);
		this.destination = destination;
	}

	/**
	 * A getter for the {@link IslandGroup} literal requested by the user.
	 * @return the chosen destination literal
	 */
	public String getDestination() {
		return destination;
	}
}
