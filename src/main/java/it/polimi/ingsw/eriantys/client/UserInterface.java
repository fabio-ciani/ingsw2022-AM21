package it.polimi.ingsw.eriantys.client;

public interface UserInterface {
	void setClient(Client client);
	void showInfo(String details);
	void showError(String details);
	void showStatus(GameStatus status);
	void getInputs();
}
