package it.polimi.ingsw.eriantys.model;

public enum Color {
	YELLOW("Gnomes"),
	BLUE("Unicorns"),
	GREEN("Frogs"),
	RED("Dragons"),
	PINK("Fairies");

	private final String realm;

	Color(String realm) {
		this.realm = realm;
	}
}
