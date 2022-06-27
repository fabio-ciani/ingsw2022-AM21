package it.polimi.ingsw.eriantys.controller;

import java.io.Serializable;

/**
 * This class contains all the relevant information about a {@link Game}. Objects of this class are meant to be included
 * in messages between the server and the clients in order to provide updates about the game lobby, and to be used by
 * classes which need information about the game object.
 */
public class GameInfo implements Serializable {
	private final int gameId;
	private final String creator;
	private final int lobbySize;
	private int currentPlayers;
	private final boolean expertMode;

	/**
	 * Constructs an instance of {@link GameInfo} with the specified parameters.
	 * @param gameId the id of the referenced {@link Game}.
	 * @param creator the creator of the referenced game.
	 * @param lobbySize the number of players expected to participate in the referenced game.
	 * @param expertMode a flag specifying whether the referenced game is in expert mode.
	 */
	public GameInfo(int gameId, String creator, int lobbySize, boolean expertMode) {
		this.gameId = gameId;
		this.creator = creator;
		this.lobbySize = lobbySize;
		this.currentPlayers = 0;
		this.expertMode = expertMode;
	}

	/**
	 * Returns the id of the referenced game.
	 * @return the id of the referenced game.
	 */
	public int getGameId() {
		return gameId;
	}

	/**
	 * Returns the number of players expected to participate in the referenced game.
	 * @return the number of players expected to participate in the referenced game.
	 */
	public int getLobbySize() {
		return lobbySize;
	}

	/**
	 * Returns the number of players currently participating in the referenced game.
	 * @return the number of players currently participating in the referenced game.
	 */
	public int getCurrentPlayers() {
		return currentPlayers;
	}

	/**
	 * Sets the number of players currently participating in the referenced game to the specified value.
	 * @param currentPlayers the new value for the current players.
	 */
	public void setCurrentPlayers(int currentPlayers) {
		this.currentPlayers = currentPlayers;
	}

	/**
	 * Returns {@code true} if and only if the game is in expert mode.
	 * @return {@code true} if and only if the game is in expert mode.
	 */
	public boolean isExpertMode() {
		return expertMode;
	}

	/**
	 * Returns the creator of the referenced game.
	 * @return the creator of the referenced game.
	 */
	public String getCreator() {
		return creator;
	}

	@Override
	public String toString() {
		return "[id: " + gameId +"] \u2192 " +
				currentPlayers + "/" +
				lobbySize + " players, expert mode " +
				(expertMode ? "enabled" : "disabled") +
				", created by " + creator;
	}
}
