package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

/**
 * A {@link ConnectionMessage} sent by a client in order to request the server to join a lobby.
 */
public class JoinLobby extends ConnectionMessage {
	private final int gameId;

	public JoinLobby(String sender, int gameId) {
		super(sender);
		this.gameId = gameId;
	}

	/**
	 * A getter for the identifier of the lobby which the user is requesting to join.
	 * @return the identifier of the game
	 */
	public int getGameId() {
		return gameId;
	}
}
