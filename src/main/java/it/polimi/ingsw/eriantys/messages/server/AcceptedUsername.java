package it.polimi.ingsw.eriantys.messages.server;

public class AcceptedUsername extends Accepted {
	private final String username;

	public AcceptedUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
