package it.polimi.ingsw.eriantys.model;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration which defines the tower color chosen by a {@link Player}.
 */
public enum TowerColor {
	BLACK, WHITE, GREY;

	public static List<String> stringLiterals() {
		return Arrays.stream(TowerColor.values()).map(TowerColor::toString).toList();
	}
}