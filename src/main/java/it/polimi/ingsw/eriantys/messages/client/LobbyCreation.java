package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

/**
 * A {@link ConnectionMessage} sent by a client in order to request the server to create a new lobby
 * with the specified number of players and game mode.
 */
public class LobbyCreation extends ConnectionMessage {
	private final int numPlayers;
	private final boolean expertMode;

	public LobbyCreation(String sender, int numPlayers, boolean expertMode) {
		super(sender);
		this.numPlayers = numPlayers;
		this.expertMode = expertMode;
	}

	/**
	 * A getter for the number of players set for the lobby which the user is requesting to create.
	 * @return the number of players which the lobby can and must hold in order to start the game
	 */
	public int getNumPlayers() {
		return numPlayers;
	}

	/**
	 * A getter for the type of mode set for the lobby which the user is requesting to create.
	 * @return {@code true} if and only if expert mode must be enabled in the game
	 */
	public boolean isExpertMode() {
		return expertMode;
	}
}
