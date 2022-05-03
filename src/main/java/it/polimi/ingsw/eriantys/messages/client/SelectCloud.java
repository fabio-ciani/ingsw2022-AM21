package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.GameMessage;

public class SelectCloud extends GameMessage {
	private final int cloud;

	public SelectCloud(String sender, int cloud) {
		super(sender);
		this.cloud = cloud;
	}

	public int getCloud() {
		return cloud;
	}
}
