package it.polimi.ingsw.eriantys.client;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.server.*;

/**
 * This interface defines a method (with overloading) to handle all different types of messages
 * that can be received by the client.
 * It should be implemented by the different user interfaces to update the view component when notified by the server.
 */
public interface ClientMessageHandler {
	/**
	 * Handles a generic {@link Message}.
	 * @param message the received message
	 */
	void handleMessage(Message message);

	/**
	 * Handles a {@link Accepted} message.
	 * @param message the received message
	 */
	void handleMessage(Accepted message);

	/**
	 * Handles a {@link AcceptedUsername} message.
	 * @param message the received message
	 */
	void handleMessage(AcceptedUsername message);

	/**
	 * Handles a {@link AcceptedJoinLobby} message.
	 * @param message the received message
	 */
	void handleMessage(AcceptedJoinLobby message);

	/**
	 * Handles a {@link AcceptedLeaveLobby} message.
	 * @param message the received message
	 */
	void handleMessage(AcceptedLeaveLobby message);

	/**
	 * Handles a {@link Refused} message.
	 * @param message the received message
	 */
	void handleMessage(Refused message);

	/**
	 * Handles a {@link RefusedReconnect} message.
	 * @param message the received message
	 */
	void handleMessage(RefusedReconnect message);

	/**
	 * Handles a {@link HelpResponse} message.
	 * @param message the received message
	 */
	void handleMessage(HelpResponse message);

	/**
	 * Handles a {@link AvailableLobbies} message.
	 * @param message the received message
	 */
	void handleMessage(AvailableLobbies message);

	/**
	 * Handles a {@link LobbyUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(LobbyUpdate message);

	/**
	 * Handles a {@link UserSelectionUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(UserSelectionUpdate message);

	/**
	 * Handles a {@link InitialBoardStatus} message.
	 * @param message the received message
	 */
	void handleMessage(InitialBoardStatus message);

	/**
	 * Handles a {@link AssistantCardUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(AssistantCardUpdate message);

	/**
	 * Handles a {@link BoardUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(BoardUpdate message);

	/**
	 * Handles a {@link CharacterCardUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(CharacterCardUpdate message);

	/**
	 * Handles a {@link LastRoundUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(LastRoundUpdate message);

	/**
	 * Handles a {@link GameOverUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(GameOverUpdate message);

	/**
	 * Handles a {@link ReconnectionUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(ReconnectionUpdate message);

	/**
	 * Handles a {@link DisconnectionUpdate} message.
	 * @param message the received message
	 */
	void handleMessage(DisconnectionUpdate message);

	/**
	 * Handles a {@link Ping} message.
	 * @param message the received message
	 */
	void handleMessage(Ping message);
}
