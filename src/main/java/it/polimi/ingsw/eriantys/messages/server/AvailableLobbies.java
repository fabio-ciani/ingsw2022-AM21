package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.client.LobbiesRequest;
import it.polimi.ingsw.eriantys.server.Server;

import java.util.List;

/**
 * A {@link Message} sent by the server in order to communicate the result of a {@link LobbiesRequest} handling process.
 */
public class AvailableLobbies extends Message {
	private final List<GameInfo> lobbies;

	public AvailableLobbies(List<GameInfo> lobbies) {
		super(Server.name);
		this.lobbies = lobbies;
	}

	/**
	 * A getter for the available lobbies on the server.
	 * @return the lobbies which can be joined in
	 */
	public List<GameInfo> getLobbies() {
		return lobbies;
	}
}
