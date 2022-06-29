package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

/**
 * A {@link GameMessage} sent by a client in order to inform the server of
 * the cloud tile literal specified by the user in the current action phase.
 */
public class SelectCloud extends GameMessage {
	private final int cloud;

	public SelectCloud(String sender, int cloud) {
		super(sender);
		this.cloud = cloud;
	}

	/**
	 * A getter for the cloud tile literal requested by the user.
	 * @return the chosen cloud tile literal
	 */
	public int getCloud() {
		return cloud;
	}
}
