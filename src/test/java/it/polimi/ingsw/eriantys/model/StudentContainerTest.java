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
	void getQuantity_StudentsOverflow_Return12() throws NoMovementException {
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
	void getQuantity_StudentsDiningRoomOverflow_Return10() throws NoMovementException {
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
	void moveColorTo_PassNullDest_ThrowException() {
		StudentContainer src = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(null, Color.YELLOW));
	}

	@Test
	void moveColorTo_PassNullColor_ThrowException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, null));
	}

	@Test
	void moveColorTo_EmptySource_ThrowException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.PINK));
	}

	@Test
	void moveColorTo_FullDestination_ThrowException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new Bag();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.YELLOW));
	}

	@Test
	void moveColorTo_FullDestinationDiningRoom_ThrowException() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new DiningRoom();

		for (int i = 0; i < 10; i++)
			new Bag().moveTo(dest, Color.RED);

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, Color.RED));
	}

	@Test
	void moveColorTo_NormalConditions_MoveStudent() throws NoMovementException {
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
	void moveAmtTo_PassNullDest_ThrowException() {
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
	void moveAmtTo_EmptySource_ThrowException() {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();

		assertThrowsExactly(NoMovementException.class, () -> src.moveTo(dest, 3));
	}

	@Test
	void moveAmtTo_FullDestination_ThrowException() {
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
	void moveAllTo_EmptySource_NoChange() throws NoMovementException {
		StudentContainer dest = new StudentContainer();
		new Bag().moveTo(dest, 4);
		assertEquals(4, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
		new StudentContainer().moveAllTo(dest);
		assertEquals(4, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void moveAllTo_FullDestination_NoChangeAndThrowException() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new Bag();
		new Bag().moveTo(src, 7);
		assertThrowsExactly(NoMovementException.class, () -> src.moveAllTo(dest));
		assertEquals(7, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(130, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void moveAllTo_Move4DestinationCapacity2_Move2AndThrowException() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new Bag();
		new Bag().moveTo(src, 4);
		dest.moveTo(new StudentContainer(), 2);

		assertEquals(4, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(128, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
		assertThrowsExactly(NoMovementException.class, () -> src.moveAllTo(dest));
		assertEquals(2, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(130, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void moveAllTo_Move4DestinationCapacity6_Move4() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new Bag();
		new Bag().moveTo(src, 4);
		dest.moveTo(new StudentContainer(), 6);

		assertEquals(4, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(124, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
		src.moveAllTo(dest);
		assertEquals(0, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(128, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void refillFrom_NullSource_ThrowException() {
		assertThrowsExactly(NoMovementException.class, () -> new StudentContainer().refillFrom(null));
	}

	@Test
	void refillFrom_EmptySource_ThrowException() {
		StudentContainer dest = new StudentContainer();
		assertThrowsExactly(NoMovementException.class, () -> dest.refillFrom(new StudentContainer()));
	}

	@Test
	void refillFrom_SourceHasLessThanEnough_MoveAllInSourceAndThrowException() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer();
		new Bag().moveTo(src, 10);
		assertThrowsExactly(NoMovementException.class, () -> dest.refillFrom(src));
		assertEquals(0, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(10, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void refillFrom_SourceHasEnough_DestinationFull() throws NoMovementException {
		StudentContainer src = new StudentContainer();
		StudentContainer dest = new StudentContainer(20);
		new Bag().moveTo(src, 30);
		dest.refillFrom(src);
		assertEquals(10, Arrays.stream(Color.values()).mapToInt(src::getQuantity).reduce(0, Integer::sum));
		assertEquals(20, Arrays.stream(Color.values()).mapToInt(dest::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void swap_PassNullArguments_ThrowException() {
		StudentContainer container = new StudentContainer();
		assertThrowsExactly(NoMovementException.class, () -> container.swap(null, Color.PINK, Color.RED));
		assertThrowsExactly(NoMovementException.class, () -> container.swap(container, null, Color.RED));
		assertThrowsExactly(NoMovementException.class, () -> container.swap(container, Color.GREEN, null));
	}

	@Test
	void swap_OneOrMoreEmptyContainers_ThrowException() throws NoMovementException {
		StudentContainer emptyContainer = new StudentContainer();
		StudentContainer nonEmptyContainer = new StudentContainer();
		new Bag().moveTo(nonEmptyContainer, 30);

		assertThrowsExactly(NoMovementException.class, () -> emptyContainer.swap(nonEmptyContainer, Color.PINK, Color.RED));
		assertThrowsExactly(NoMovementException.class, () -> nonEmptyContainer.swap(emptyContainer, Color.BLUE, Color.RED));
		assertThrowsExactly(NoMovementException.class, () -> emptyContainer.swap(emptyContainer, Color.GREEN, Color.PINK));
	}

	@Test
	void swap_OneOrMoreFullContainers_ThrowException() throws NoMovementException {
		StudentContainer nonFullContainer = new StudentContainer();
		StudentContainer fullContainer = new StudentContainer();
		new Bag().moveTo(nonFullContainer, 30);
		fullContainer.fill();

		assertThrowsExactly(NoMovementException.class, () -> fullContainer.swap(nonFullContainer, Color.GREEN, Color.RED));
		assertThrowsExactly(NoMovementException.class, () -> nonFullContainer.swap(fullContainer, Color.BLUE, Color.YELLOW));
		assertThrowsExactly(NoMovementException.class, () -> fullContainer.swap(fullContainer, Color.BLUE, Color.PINK));
	}

	@Test
	void swap_FullContainersSwapSameColor_NoChange() {
		StudentContainer cont1 = new StudentContainer();
		StudentContainer cont2 = new StudentContainer();
		cont1.fill();
		cont2.fill();

		assertDoesNotThrow(() -> cont1.swap(cont2, Color.PINK, Color.PINK));
		for (Color color : Color.values()) {
			assertEquals(26, cont1.getQuantity(color));
			assertEquals(26, cont2.getQuantity(color));
		}
	}

	@Test
	void swap_ContainersHaveEnoughStudentsAndCapacity_SuccessfulSwap() throws NoMovementException {
		StudentContainer cont1 = new StudentContainer();
		StudentContainer cont2 = new StudentContainer();

		new Bag().moveTo(cont1, Color.BLUE);
		new Bag().moveTo(cont2, Color.PINK);

		cont1.swap(cont2, Color.BLUE, Color.PINK);

		for (Color color : Color.values()) {
			if (color == Color.BLUE) {
				assertEquals(0, cont1.getQuantity(color));
				assertEquals(1, cont2.getQuantity(color));
			} else if (color == Color.PINK) {
				assertEquals(1, cont1.getQuantity(color));
				assertEquals(0, cont2.getQuantity(color));
			} else {
				assertEquals(0, cont1.getQuantity(color));
				assertEquals(0, cont2.getQuantity(color));
			}
		}
	}

	@Test
	void remainingCapacity_EmptyContainer_ReturnMaxSize() {
		assertEquals(130, new StudentContainer().remainingCapacity(Color.PINK));
	}

	@Test
	void remainingCapacity_FullContainer_Return0() {
		assertEquals(0, new Bag().remainingCapacity(Color.GREEN));
	}

	@Test
	void remainingCapacity_Contains50_Return80() throws NoMovementException {
		StudentContainer container = new StudentContainer();
		new Bag().moveTo(container, 50);
		assertEquals(80, container.remainingCapacity(Color.YELLOW));
	}

	@Test
	void remainingCapacity_OverflowContainer_ThrowExceptionAndReturn0() {
		StudentContainer container = new StudentContainer(30);
		assertThrowsExactly(NoMovementException.class, () -> new Bag().moveTo(container, 50));
		assertEquals(0, container.remainingCapacity(Color.BLUE));
	}

	@Test
	void remainingCapacity_PassNullAndContains30_Return100() throws NoMovementException {
		StudentContainer container = new StudentContainer();
		new Bag().moveTo(container, 30);
		assertEquals(100, container.remainingCapacity(Color.RED));
	}

	@Test
	void fill_NoMaxSize_FillTo130() {
		StudentContainer container = new StudentContainer();
		container.fill();
		assertEquals(130,
					Arrays.stream(Color.values()).mapToInt(container::getQuantity).reduce(0, Integer::sum));
	}
}