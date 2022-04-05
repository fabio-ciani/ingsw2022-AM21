package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
	@Test
	void construct() {
		Bag bag = new Bag();
		for (Color color : Color.values())
			assertEquals(26, bag.getQuantity(color));
	}

	@Test
	void setupDrawTestRemainder() {
		Bag bag = new Bag();
		bag.setupDraw();
		for (Color color : Color.values())
			assertEquals(26, bag.getQuantity(color));
	}

	@Test
	void setupDrawTestResult() {
		List<Color> res = new Bag().setupDraw();
		for (Color color : Color.values()) {
			int n = res.stream().filter(c -> c == color).toList().size();
			assertEquals(2, n);
		}
	}
}