package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import java.util.List;

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

	@Test
	void remainingCapacity_EmptyBag_Return26() throws NoMovementException {
		Bag bag = new Bag();
		bag.moveTo(new StudentContainer(), 130);
		for (Color color : Color.values())
			assertEquals(26, bag.remainingCapacity(color));
	}

	@Test
	void remainingCapacity_FullBag_Return0() {
		Bag bag = new Bag();
		for (Color color : Color.values())
			assertEquals(0, bag.remainingCapacity(color));
	}

	@Test
	void remainingCapacity_NotEmptyNotFull_ReturnActualValue() throws NoMovementException {
		Bag bag = new Bag();

		for (int i = 0; i < 15; i++)
			bag.moveTo(new StudentContainer(), Color.PINK);

		assertEquals(15, bag.remainingCapacity(Color.PINK));
	}

	@Test
	void remainingCapacity_PassNull_ReturnNeg1() {
		assertEquals(-1, new Bag().remainingCapacity(null));
	}
}