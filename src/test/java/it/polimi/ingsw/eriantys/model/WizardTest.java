package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WizardTest {

	@Test
	void stringLiterals_NormalPostConditions() {
		List<String> literals = Wizard.stringLiterals();

		assertEquals("FOREST", literals.get(0));
		assertEquals("DESERT", literals.get(1));
		assertEquals("SKY", literals.get(2));
		assertEquals("SNOW", literals.get(3));
	}
}