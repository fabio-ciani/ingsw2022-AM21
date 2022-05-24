package it.polimi.ingsw.eriantys.client.gui;

public enum SceneName {
	LOGIN("login.fxml"),
	LOBBIES("lobbies.fxml"),
	WAITING_ROOM("waiting_room.fxml"),
	TOWERS("towers.fxml"),
	WIZARDS("wizards.fxml"),
	ASSISTANT_CARDS("assistant_cards.fxml"),
	SCHOOLBOARD("schoolboard.fxml"),
	ISLANDS("islands.fxml"),
	CHARACTER_CARDS("character_cards.fxml");

	private final String path;

	SceneName(String path) {
		this.path = "/fxml/" + path;
	}

	public String getPath() {
		return path;
	}
}
