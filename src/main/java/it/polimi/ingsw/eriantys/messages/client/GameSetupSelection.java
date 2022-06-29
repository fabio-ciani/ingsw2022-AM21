package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

/**
 * A {@link GameMessage} sent by a client in order to inform the server the tower color and wizard chosen by the user.
 */
public class GameSetupSelection extends GameMessage {
	private final String towerColor;
	private final String wizard;

	public GameSetupSelection(String sender, String towerColor, String wizard) {
		super(sender);
		this.towerColor = towerColor;
		this.wizard = wizard;
	}

	/**
	 * A getter for the tower color requested by the user.
	 * @return the chosen tower color
	 */
	public String getTowerColor() {
		return towerColor;
	}

	/**
	 * A getter for the wizard requested by the user.
	 * @return the chosen wizard
	 */
	public String getWizard() {
		return wizard;
	}
}
