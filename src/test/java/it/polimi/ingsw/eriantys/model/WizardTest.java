package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WizardTest {

	@Test
	void stringLiterals_NormalPostConditions() {
		List<String> literals = Wizard.stringLiterals();

		assertEquals("FOREST_WIZARD", literals.get(0));
		assertEquals("DESERT_WIZARD", literals.get(1));
		assertEquals("SKY_WIZARD", literals.get(2));
		assertEquals("SNOW_WIZARD", literals.get(3));
	}
}