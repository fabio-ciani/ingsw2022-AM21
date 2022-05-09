package it.polimi.ingsw.eriantys.controller;

import java.io.Serializable;

public class GameInfo implements Serializable {
	private final int gameId;
	private final String creator;
	private final int lobbySize;
	private final boolean expertMode;

	public GameInfo(int gameId, String creator, int lobbySize, boolean expertMode) {
		this.gameId = gameId;
		this.creator = creator;
		this.lobbySize = lobbySize;
		this.expertMode = expertMode;
	}

	public int getGameId() {
		return gameId;
	}

	public int getLobbySize() {
		return lobbySize;
	}

	public boolean isExpertMode() {
		return expertMode;
	}

	public String getCreator() {
		return creator;
	}

	@Override
	public String toString() {
		return "GameInfo{" +
				"gameId=" + gameId +
				", creator='" + creator + '\'' +
				", lobbySize=" + lobbySize +
				", expertMode=" + expertMode +
				'}';
	}
}
