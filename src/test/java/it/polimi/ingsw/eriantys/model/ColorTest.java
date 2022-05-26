package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColorTest {

	@Test
	void stringLiterals_NormalPostConditions() {
		List<String> literals = Color.stringLiterals();

		assertEquals("YELLOW", literals.get(0));
		assertEquals("BLUE", literals.get(1));
		assertEquals("GREEN", literals.get(2));
		assertEquals("RED", literals.get(3));
		assertEquals("PINK", literals.get(4));
	}
}