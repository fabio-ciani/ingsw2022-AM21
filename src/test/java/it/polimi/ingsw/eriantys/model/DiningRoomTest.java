package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiningRoomTest {

	@Test
	void checkForCoinsTestEmpty() {
		assertFalse(new DiningRoom().checkForCoins(Color.RED));
	}

	@Test
	void checkForCoinsTestTrue() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 6; i++)
			bag.moveTo(diningRoom, Color.GREEN);

		assertTrue(diningRoom.checkForCoins(Color.GREEN));
		for (Color color : Color.values())
			if (color != Color.GREEN)
				assertFalse(diningRoom.checkForCoins(color));
	}

	@Test
	void checkForCoinsTestNull() {
		assertFalse(new DiningRoom().checkForCoins(null));
	}

	@Test
	void remainingCapacityTestEmpty() {
		assertEquals(10, new DiningRoom().remainingCapacity(Color.YELLOW));
	}

	@Test
	void remainingCapacityTestFull() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 10; i++)
			bag.moveTo(diningRoom, Color.BLUE);

		assertEquals(0, diningRoom.remainingCapacity(Color.BLUE));
	}

	@Test
	void remainingCapacityTestOther() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 7; i++)
			bag.moveTo(diningRoom, Color.PINK);

		assertEquals(3, diningRoom.remainingCapacity(Color.PINK));
	}

	@Test
	void remainingCapacityTestOverflow() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 10; i++)
			bag.moveTo(diningRoom, Color.GREEN);

		assertThrowsExactly(NoMovementException.class, () -> bag.moveTo(diningRoom, Color.GREEN));

		assertEquals(0, diningRoom.remainingCapacity(Color.GREEN));
	}

	@Test
	void remainingCapacityTestNull() {
		assertEquals(11, new DiningRoom().remainingCapacity(null));
	}
}