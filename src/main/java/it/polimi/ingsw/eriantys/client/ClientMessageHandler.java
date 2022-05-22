package it.polimi.ingsw.eriantys.client;

import it.polimi.ingsw.eriantys.messages.Message;
import it.polimi.ingsw.eriantys.messages.Ping;
import it.polimi.ingsw.eriantys.messages.server.*;

public interface ClientMessageHandler {
	void handleMessage(Message message);
	void handleMessage(Accepted message);
	void handleMessage(AcceptedUsername message);
	void handleMessage(AcceptedJoinLobby message);
	void handleMessage(AcceptedLeaveLobby message);
	void handleMessage(Refused message);
	void handleMessage(RefusedReconnect message);
	void handleMessage(HelpResponse message);
	void handleMessage(AvailableLobbies message);
	void handleMessage(LobbyUpdate message);
	void handleMessage(AssistantCardUpdate message);
	void handleMessage(BoardUpdate message);
	void handleMessage(CharacterCardUpdate message);
	void handleMessage(UserSelectionUpdate message);
	void handleMessage(GameOverUpdate message);
	void handleMessage(InitialBoardStatus message);
	void handleMessage(ReconnectionUpdate message);
	void handleMessage(DisconnectionUpdate message);
	void handleMessage(Ping message);
}
