package it.polimi.ingsw.eriantys.client;

import it.polimi.ingsw.eriantys.messages.Message;

public interface UserInterface {
	void setClient(Client client);
	void showInfo(String details);
	void showError(String details);
	void showStatus(GameStatus status);
	void getInputs();
	void handleMessage(Message message);
}
