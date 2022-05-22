package it.polimi.ingsw.eriantys.client;

public interface UserInterface extends Runnable, ClientMessageHandler {
	void setClient(Client client);
	void showInfo(String details);
	void showError(String details);

	@Override
	void run();
}
