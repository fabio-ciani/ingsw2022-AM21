package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TowerColorTest {

	@Test
	void stringLiterals_NormalPostConditions() {
		List<String> literals = TowerColor.stringLiterals();

		assertEquals("BLACK", literals.get(0));
		assertEquals("WHITE", literals.get(1));
		assertEquals("GREY", literals.get(2));
	}
}