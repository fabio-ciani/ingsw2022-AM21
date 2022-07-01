package it.polimi.ingsw.eriantys.model;

import java.util.Arrays;
import java.util.List;

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

	/**
	 * A method to convert the enumeration values into {@link String}.
	 * @return a {@link List} containing the enumeration values
	 */
	public static List<String> stringLiterals() {
		return Arrays.stream(values()).map(Enum::toString).toList();
	}
}
