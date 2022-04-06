package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiningRoomTest {

	@Test
	void checkForCoins_EmptyDiningRoom_ReturnFalseForAllColors() {
		DiningRoom diningRoom = new DiningRoom();
		for (Color color : Color.values())
			assertFalse(diningRoom.checkForCoins(color));
	}

	@Test
	void checkForCoins_6GreenStudents_ReturnTrueOnlyForGreen() throws NoMovementException {
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
	void checkForCoins_PassNull_ReturnFalse() {
		assertFalse(new DiningRoom().checkForCoins(null));
	}

	@Test
	void remainingCapacity_EmptyDiningRoom_Return10() {
		assertEquals(10, new DiningRoom().remainingCapacity(Color.YELLOW));
	}

	@Test
	void remainingCapacity_FullDiningRoom_Return0() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 10; i++)
			bag.moveTo(diningRoom, Color.BLUE);

		assertEquals(0, diningRoom.remainingCapacity(Color.BLUE));
	}

	@Test
	void remainingCapacity_Has7PinkStudents_Return7() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 7; i++)
			bag.moveTo(diningRoom, Color.PINK);

		assertEquals(3, diningRoom.remainingCapacity(Color.PINK));
	}

	@Test
	void remainingCapacity_FullDiningRoomWithOverflow_Return0() throws NoMovementException {
		DiningRoom diningRoom = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 10; i++)
			bag.moveTo(diningRoom, Color.GREEN);

		assertThrowsExactly(NoMovementException.class, () -> bag.moveTo(diningRoom, Color.GREEN));

		assertEquals(0, diningRoom.remainingCapacity(Color.GREEN));
	}

	@Test
	void remainingCapacity_PassNull_ReturnNeg1() {
		assertEquals(-1, new DiningRoom().remainingCapacity(null));
	}
}