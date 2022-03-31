package it.polimi.ingsw.eriantys.model;

/**
 * The possible colors of the student discs and the professors, with the relative {@link Color#realm}.
 */
public enum Color {
	YELLOW("Gnomes"),
	BLUE("Unicorns"),
	GREEN("Frogs"),
	RED("Dragons"),
	PINK("Fairies");

	private final String realm;

	/**
	 * Constructs a color with its relative {@code realm}.
	 * @param realm the color's {@code realm}.
	 */
	Color(String realm) {
		this.realm = realm;
	}
}
