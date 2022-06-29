package it.polimi.ingsw.eriantys.messages.server;

/**
 * A message of type {@link ConnectionUpdate} sent by the server in order to broadcast a reconnection event.
 */
public class ReconnectionUpdate extends ConnectionUpdate {
	private final boolean gameResumed;
	public ReconnectionUpdate(String subject, int numPlayers, boolean gameResumed) {
		super(subject, numPlayers);
		this.gameResumed = gameResumed;
	}

	/**
	 * A getter for the game status.
	 * @return true if and only if the game has resumed following the event
	 */
	public boolean isGameResumed() {
		return gameResumed;
	}
}
