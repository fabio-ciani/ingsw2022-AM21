package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {

	@Test
	void construct_NormalConditions_Has6TowersAndEmptyEntranceAndDiningRoom() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);

		for (int i = 0; i < 6; i++)
			assertTrue(schoolBoard.getTower());
		assertFalse(schoolBoard.getTower());

		StudentContainer entrance = schoolBoard.getEntrance();
		DiningRoom diningRoom = schoolBoard.getDiningRoom();
		for (Color color : Color.values()) {
			assertEquals(0, entrance.getQuantity(color));
			assertEquals(9, entrance.remainingCapacity(color));

			assertEquals(0, diningRoom.getQuantity(color));
			assertEquals(10, diningRoom.remainingCapacity(color));
		}
	}

	@Test
	void getEntrance_NormalConditions_ReturnNonNullStudentContainer() {
		assertNotNull(new SchoolBoard(9, 6).getEntrance());
	}

	@Test
	void getDiningRoom_NormalConditions_ReturnNonNullDiningRoom() {
		assertNotNull(new SchoolBoard(9, 6).getDiningRoom());
	}

	@Test
	void checkForCoins_IsEmpty_ReturnFalse() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);
		for (Color color : Color.values())
			assertFalse(schoolBoard.checkForCoins(color));
	}

	@Test
	void checkForCoins_PassNull_ReturnFalse() {
		assertFalse(new SchoolBoard(9, 6).checkForCoins(null));
	}

	@Test
	void checkForCoins_Has4Students_ReturnFalse() throws InvalidArgumentException, NoMovementException {
		Bag bag = new Bag();
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);
		DiningRoom diningRoom = schoolBoard.getDiningRoom();

		for (int i = 0; i < 4; i++)
			bag.moveTo(diningRoom, Color.RED);

		for (Color color : Color.values())
			assertFalse(schoolBoard.checkForCoins(color));
	}

	@Test
	void checkForCoins_Has9Students_ReturnTrue() throws InvalidArgumentException, NoMovementException {
		Bag bag = new Bag();
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);
		DiningRoom diningRoom = schoolBoard.getDiningRoom();

		for (int i = 0; i < 9; i++)
			bag.moveTo(diningRoom, Color.PINK);

		for (Color color : Color.values())
			if (color != Color.PINK)
				assertFalse(schoolBoard.checkForCoins(color));
		else
				assertTrue(schoolBoard.checkForCoins(color));
	}

	@Test
	void getTower_HasAllTowers_ReturnTrue() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);
		assertTrue(schoolBoard.getTower());
	}

	@Test
	void getTower_HasNoTowers_ReturnFalse() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);

		for(int i = 0; i < 6; i++)
			assertTrue(schoolBoard.getTower());
		assertFalse(schoolBoard.getTower());
	}

	@Test
	void putTower_HasAllTowers_ReturnFalse() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);
		assertFalse(schoolBoard.putTower());
	}

	@Test
	void putTower_HasNoTowers_ReturnTrue() {
		SchoolBoard schoolBoard = new SchoolBoard(9, 6);

		for(int i = 0; i < 6; i++)
			assertTrue(schoolBoard.getTower());
		for(int i = 0; i < 6; i++)
			assertTrue(schoolBoard.putTower());
		assertFalse(schoolBoard.putTower());
	}
}