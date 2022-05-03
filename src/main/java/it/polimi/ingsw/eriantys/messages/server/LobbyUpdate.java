package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

import java.util.List;

public class LobbyUpdate extends Message {
	private final List<String> players;

	public LobbyUpdate(List<String> players) {
		super(Server.name);
		this.players = players;
	}

	public List<String> getPlayers() {
		return players;
	}
}
