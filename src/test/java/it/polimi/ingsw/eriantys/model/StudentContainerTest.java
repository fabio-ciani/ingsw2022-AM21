package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StudentContainerTest {
	@Test
	void getQuantity_EmptyContainer_Return0() {
		for (Color color : Color.values())
			assertEquals(0, new StudentContainer().getQuantity(color));
	}

	@Test
	void getQuantity_Add4Students_Return4() throws NoMovementException {
		StudentContainer container = new StudentContainer(12);
		Bag bag = new Bag();

		for (int i = 0; i < 4; i++)
			bag.moveTo(container, Color.GREEN);

		for (Color color : Color.values())
			if (color != Color.GREEN)
				assertEquals(0, container.getQuantity(color));
			else
				assertEquals(4, container.getQuantity(color));
	}

	@Test
	void getQuantity_OverflowStudents_Return12() throws NoMovementException {
		StudentContainer container = new StudentContainer(12);
		Bag bag = new Bag();

		for (int i = 0; i < 12; i++)
			bag.moveTo(container, Color.GREEN);
		for (int i = 0; i < 2; i++)
			assertThrowsExactly(NoMovementException.class, () -> bag.moveTo(container, Color.GREEN));

		for (Color color : Color.values())
			if (color != Color.GREEN)
				assertEquals(0, container.getQuantity(color));
			else
				assertEquals(12, container.getQuantity(color));
	}

	@Test
	void getQuantity_OverflowStudentsDiningRoom_Return10() throws NoMovementException {
		StudentContainer container = new DiningRoom();
		Bag bag = new Bag();

		for (int i = 0; i < 10; i++)
			bag.moveTo(container, Color.BLUE);
		for (int i = 0; i < 2; i++)
			assertThrowsExactly(NoMovementException.class, () -> bag.moveTo(container, Color.BLUE));

		for (Color color : Color.values())
			if (color != Color.BLUE)
				assertEquals(0, container.getQuantity(color));
			else
				assertEquals(10, container.getQuantity(color));
	}

	@Test
	void moveColorTo_PassNullDest_ThrowsException() {
		StudentContainer src = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(null, Color.YELLOW));
	}

	@Test
	void moveColorTo_PassNullColor_ThrowsException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, null));
	}

	@Test
	void moveColorTo_EmptySource_ThrowsException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.PINK));
	}

	@Test
	void moveColorTo_FullDestination_ThrowsException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new Bag();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.YELLOW));
	}

	@Test
	void moveColorTo_FullDestinationDiningRoom_ThrowsException() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new DiningRoom();

		for (int i = 0; i < 10; i++)
			new Bag().moveTo(dest, Color.RED);

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.RED));
	}

	@Test
	void moveColorTo_NormalConditions_MovesStudent() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();
		Bag bag = new Bag();

		bag.moveTo(src, Color.BLUE);
		src.moveTo(dest, Color.BLUE);

		for (Color color : Color.values())
			if (color == Color.BLUE) {
				assertEquals(25, bag.getQuantity(color));
				assertEquals(0, src.getQuantity(color));
				assertEquals(1, dest.getQuantity(color));
			} else {
				assertEquals(26, bag.getQuantity(color));
				assertEquals(0, src.getQuantity(color));
				assertEquals(0, dest.getQuantity(color));
			}
	}

	@Test
	void moveAmtTo_PassNullDest_ThrowsException() {
		StudentContainer src = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(null, 4));
	}

	@Test
	void moveAmtTo_PassNegative_NoChange() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();
		Bag bag = new Bag();

		bag.moveTo(src, Color.RED);
		bag.moveTo(dest, Color.BLUE);

		src.moveTo(dest, -1);

		assertEquals(1, src.getQuantity(Color.RED));
		assertEquals(1, dest.getQuantity(Color.BLUE));
	}

	@Test
	void moveAmtTo_PassZero_NoChange() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();
		Bag bag = new Bag();

		bag.moveTo(src, Color.RED);
		bag.moveTo(dest, Color.BLUE);

		src.moveTo(dest, 0);

		assertEquals(1, src.getQuantity(Color.RED));
		assertEquals(1, dest.getQuantity(Color.BLUE));
	}

	@Test
	void moveAmtTo_EmptySource_ThrowsException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, 3));
	}

	@Test
	void moveAmtTo_FullDestination_ThrowsException() {
		StudentContainer src = new Bag();
		StudentContainer dest = new Bag();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, 1));
	}

	@Test
	void moveAmtTo_Pass3_TotalQuantityIncreasedBy3() throws NoMovementException {
		StudentContainer src = new Bag();
		StudentContainer dest = new StudentContainer();

		src.moveTo(dest, 3);
		assertEquals(3, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void moveAllTo() {
	}

	@Test
	void refillFrom() {
	}

	@Test
	void swap() {
	}

	@Test
	void remainingCapacity() {
	}

	@Test
	void fill() {
	}
}