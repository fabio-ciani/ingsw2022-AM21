package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.GameConstants;
import it.polimi.ingsw.eriantys.model.IslandGroup;

/**
 * A {@link GameMessage} sent by a client in order to inform the server of
 * a (single) student disc movement specified by the user in the current action phase.
 */
public class MoveStudent extends GameMessage {
	private final String color;
	private final String destination;

	public MoveStudent(String sender, String color, String destination) {
		super(sender);
		this.color = color;
		this.destination = destination;
	}

	/**
	 * A getter for the {@link Color} literal of the student requested by the user.
	 * @return the chosen {@link Color} literal
	 */
	public String getColor() {
		return color;
	}

	/**
	 * A getter for the destination literal requested by the user.
	 * The destination could be an {@link IslandGroup} literal or the {@link GameConstants} object referred to the dining room.
	 * @return the chosen destination literal
	 */
	public String getDestination() {
		return destination;
	}
}
