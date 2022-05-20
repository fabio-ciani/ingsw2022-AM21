package it.polimi.ingsw.eriantys.controller.phases;

import it.polimi.ingsw.eriantys.messages.GameMessage;
import it.polimi.ingsw.eriantys.messages.server.UserActionUpdate;
import it.polimi.ingsw.eriantys.server.exceptions.NoConnectionException;

/**
 * The processing of client-side commands by the controller is operated by a message handler entity.
 * This interface encloses a state pattern, which will be implemented by concrete classes.
 */
public interface MessageHandler {
	/**
	 * Handles and responds to the specified message received by the client, according to the rules regarding the current
	 * phase of the game.
	 * @param m the {@link GameMessage} received by the client.
	 * @throws NoConnectionException if no connection can be retrieved for the sender of the message.
	 */
	void handle(GameMessage m) throws NoConnectionException;

	/**
	 * Returns a help message which lists the possible user actions for the current phase of the game.
	 * @return a help message which lists the possible user actions for the current phase of the game.
	 */
	String getHelp();

	/**
	 * Plays the specified user's turn according to the rules if they are disconnected and the game is not idle.
	 * @param username the disconnected user's username.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	void handleDisconnectedUser(String username) throws NoConnectionException;

	/**
	 * Sends a broadcast update after the specified user's reconnection in order to make sure every player has the
	 * information they need in order to play.
	 * @param username the reconnected user's username.
	 * @throws NoConnectionException if no connection can be retrieved for one or more players.
	 */
	void sendReconnectUpdate(String username) throws NoConnectionException;
}