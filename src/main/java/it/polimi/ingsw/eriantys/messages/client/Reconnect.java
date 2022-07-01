package it.polimi.ingsw.eriantys.messages.client;

import it.polimi.ingsw.eriantys.messages.ConnectionMessage;

/**
 * A {@link ConnectionMessage} sent by a client in order to request the server to reconnect to a lobby.
 */
public class Reconnect extends ConnectionMessage {
	private final int gameId;
	private final String passcode;

	public Reconnect(String sender, int gameId, String passcode) {
		super(sender);
		this.gameId = gameId;
		this.passcode = passcode;
	}

	/**
	 * A getter for the identifier of the lobby which the user is requesting to reconnect.
	 * @return the identifier of the game
	 */
	public int getGameId() {
		return gameId;
	}

	/**
	 * A getter for the passcode which has been previously associated with the user to manage disconnection scenarios.
	 * @return the hexadecimal passcode which was given by the disconnection handling infrastructure to the user
	 */
	public String getPasscode() {
		return passcode;
	}
}
