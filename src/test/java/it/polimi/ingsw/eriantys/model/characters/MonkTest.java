package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonkTest {
	Bag bag;
	ContainerCharacterCard card;
	final int MAX_PER_COLOR = 26;

	@BeforeEach
	void init() {
		bag = new Bag();
		card = new Monk(bag);
	}

	@Test
	void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
		Color student = Color.RED;
		IslandGroup island = new IslandGroup("01");
		int initialCost = card.getCost();
		for (int i = 0; i < 3; i++) {
			assertDoesNotThrow(() -> {
				bag.moveTo(card, student);
				card.applyEffect(null, null, student, island);
			});
			assertEquals(initialCost + 1, card.getCost());
		}
	}

	/**
	 * Tests {@link Monk#applyEffect(List, List, Color, IslandGroup)} method giving the expected arguments
	 * (the student to move and the island where to move it).
	 *
	 * Checks that the target student has been removed from the {@link Monk} card and added to the target island and
	 * that another students is drawn from the {@link Bag} and placed on the card.
	 */
	@Test
	void applyEffect_TargetStudentAndIsland_StudentMoved() {
		Color student = Color.BLUE;
		IslandGroup island = new IslandGroup("01");
		assertDoesNotThrow(() -> {
			bag.moveTo(card, student);
			card.applyEffect(null, null, student, island);
		});
		int totalOnCard = 0;
		for (Color color : Color.values()) {
			totalOnCard += card.getQuantity(color);
			assertEquals(MAX_PER_COLOR, bag.getQuantity(color) + card.getQuantity(color) + island.getQuantity(color));
			assertEquals(color.equals(student) ? 1 : 0, island.getQuantity(color));
		}
		assertEquals(1, totalOnCard);
	}

	@Test
	void applyEffect_NullTargetColorOrIsland_InvalidArgumentException() {
		Color student = Color.GREEN;
		IslandGroup island = new IslandGroup("01");
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, student, null));
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, island));
	}

	@Test
	void applyEffect_StudentNotOnCard_NoMovementException() {
		Color student = Color.YELLOW;
		IslandGroup island = new IslandGroup("01");
		assertThrows(NoMovementException.class, () -> card.applyEffect(null, null, student, island));
		assertDoesNotThrow(() -> bag.moveTo(card, Color.BLUE));
		assertThrows(NoMovementException.class, () -> card.applyEffect(null, null, student, island));
	}
}