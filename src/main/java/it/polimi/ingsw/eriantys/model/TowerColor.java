package it.polimi.ingsw.eriantys.model;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration which defines the tower color chosen by a {@link Player}.
 */
public enum TowerColor {
	BLACK, WHITE, GREY;

	/**
	 * A method to convert the enumeration values into {@link String}.
	 * @return a {@link List} containing the enumeration values
	 */
	public static List<String> stringLiterals() {
		return Arrays.stream(values()).map(Enum::toString).toList();
	}
}