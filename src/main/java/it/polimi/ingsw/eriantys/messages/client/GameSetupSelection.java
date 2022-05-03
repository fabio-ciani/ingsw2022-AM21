package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

public class GameSetupSelection extends GameMessage {
	private final String towerColor;
	private final String wizard;

	public GameSetupSelection(String sender, String towerColor, String wizard) {
		super(sender);
		this.towerColor = towerColor;
		this.wizard = wizard;
	}

	public String getTowerColor() {
		return towerColor;
	}

	public String getWizard() {
		return wizard;
	}
}
