package it.polimi.ingsw.eriantys.client.gui;

/**
 * An enumeration to map {@code *.fxml} files to a name.
 * The sources contain all the information and settings to render a scene.
 */
public enum SceneName {
	LOGIN("login.fxml"),
	LOBBIES("lobbies.fxml"),
	WAITING_ROOM("waiting_room.fxml"),
	SCHOOLBOARD("schoolboard.fxml"),
	BOARD("board.fxml"),
	CHARACTER_CARDS("character_cards.fxml");

	private final String path;

	SceneName(String path) {
		this.path = "/fxml/" + path;
	}

	/**
	 * A getter for the relative path of the literal.
	 * The method can be used to load a target scene into the GUI application.
	 * @return the relative path to the {@code *.fxml} file
	 */
	public String getPath() {
		return path;
	}
}
