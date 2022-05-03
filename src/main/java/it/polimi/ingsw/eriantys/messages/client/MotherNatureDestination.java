package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

public class MotherNatureDestination extends GameMessage {
	private final String destination;

	public MotherNatureDestination(String sender, String destination) {
		super(sender);
		this.destination = destination;
	}

	public String getDestination() {
		return destination;
	}
}
