package it.polimi.ingsw.eriantys.model;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration which defines the available card backs.
 */
public enum Wizard {
	FOREST_WIZARD, DESERT_WIZARD, SKY_WIZARD, SNOW_WIZARD;

	public static List<String> stringLiterals() {
		return Arrays.stream(Wizard.values()).map(Wizard::toString).toList();
	}
}