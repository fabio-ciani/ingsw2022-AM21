package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

public class MoveStudent extends GameMessage {
	private final String color;
	private final String destination;

	public MoveStudent(String sender, String color, String destination) {
		super(sender);
		this.color = color;
		this.destination = destination;
	}

	public String getColor() {
		return color;
	}

	public String getDestination() {
		return destination;
	}
}
