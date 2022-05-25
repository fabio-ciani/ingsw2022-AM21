package it.polimi.ingsw.eriantys.client.gui;

public enum PopupName {
	TOWERS("towers.fxml"),
	WIZARDS("wizards.fxml"),
	ASSISTANT_CARDS("assistant_cards.fxml");

	private final String path;

	PopupName(String path) {
		this.path = "/fxml/" + path;
	}

	public String getPath() {
		return path;
	}
}
