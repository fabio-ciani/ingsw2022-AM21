package it.polimi.ingsw.eriantys.client.gui;

/**
 * An enumeration to map {@code *.fxml} files to a name.
 * The sources contain all the information and settings to render a popup scene.
 */
public enum PopupName {
	TOWERS("towers.fxml"),
	WIZARDS("wizards.fxml"),
	ASSISTANT_CARDS("assistant_cards.fxml");

	private final String path;

	PopupName(String path) {
		this.path = "/fxml/" + path;
	}

	/**
	 * A getter for the relative path of the literal.
	 * The method can be used to load a target popup scene into the GUI application.
	 * @return the relative path to the {@code *.fxml} file
	 */
	public String getPath() {
		return path;
	}
}
