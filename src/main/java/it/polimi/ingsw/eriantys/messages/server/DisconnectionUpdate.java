package it.polimi.ingsw.eriantys.messages.server;

/**
 * A message of type {@link ConnectionUpdate} sent by the server in order to broadcast a reconnection event.
 */
public class DisconnectionUpdate extends ConnectionUpdate {
	private final boolean gameIdle;

	public DisconnectionUpdate(String subject, int numPlayers, boolean gameIdle) {
		super(subject, numPlayers);
		this.gameIdle = gameIdle;
	}

	/**
	 * A getter for the game status.
	 * @return true if and only if the game has been set in an idle state following the event
	 */
	public boolean isGameIdle() {
		return gameIdle;
	}
}
