package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

import java.util.List;

/**
 * A {@link Message} sent by the server in order to communicate the entry or exit of a user in the lobby.
 */
public class LobbyUpdate extends Message {
	private final List<String> players;

	public LobbyUpdate(List<String> players) {
		super(Server.name);
		this.players = players;
	}

	/**
	 * A getter for the usernames of the players inside the lobby following the event.
	 * @return the player usernames
	 */
	public List<String> getPlayers() {
		return players;
	}
}
