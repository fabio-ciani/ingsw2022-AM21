package it.polimi.ingsw.eriantys.messages.server;

import it.polimi.ingsw.eriantys.messages.client.JoinLobby;
import it.polimi.ingsw.eriantys.messages.client.Reconnect;

/**
 * A message of type {@link Accepted} sent by the server in order to
 * tell the client the acceptance of a {@link JoinLobby} or {@link Reconnect} request.
 */
public class AcceptedJoinLobby extends Accepted {
	private final int gameId;
	private final String passcode;

	public AcceptedJoinLobby(int gameId, String passcode) {
		super();
		this.gameId = gameId;
		this.passcode = passcode;
	}

	/**
	 * A getter for the identifier of the lobby which the client requested to join or reconnect.
	 * @return the identifier of the game
	 */
	public int getGameId() {
		return gameId;
	}

	/**
	 * A getter for the passcode which has been previously associated to the client to manage disconnection scenarios.
	 * @return the hexadecimal passcode which was given by the disconnection handling infrastructure to the client
	 */
	public String getPasscode() {
		return passcode;
	}
}
