package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.controller.GameInfo;
import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.server.Server;

import java.util.ArrayList;
import java.util.List;

public class AvailableLobbies extends Message {
	private final List<GameInfo> lobbies;

	public AvailableLobbies(List<GameInfo> lobbies) {
		super(Server.name);
		this.lobbies = lobbies;
	}

	public List<GameInfo> getLobbies() {
		return lobbies;
	}
}
