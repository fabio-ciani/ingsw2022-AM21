package it.polimi.ingsw.eriantys.model;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration which defines the available card backs.
 */
public enum Wizard {
	FOREST_WIZARD, DESERT_WIZARD, SKY_WIZARD, SNOW_WIZARD;

	/**
	 * A method to convert the enumeration values into {@link String}.
	 * @return a {@link List} containing the enumeration values
	 */
	public static List<String> stringLiterals() {
		return Arrays.stream(values()).map(Enum::toString).toList();
	}
}