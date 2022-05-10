package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThiefTest {
	Player testPlayer;
	List<Player> players;
	Bag bag;
	BaseCharacterCard card;

	@BeforeEach
	void init() {
		testPlayer = new Player("Nick", 9, 6);
		players = List.of(
				testPlayer,
				new Player("Name", 9, 6)
		);
		bag = new Bag();
		card = new Thief(players, bag);
	}

	@Test
	void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
		final int initialCost = card.getCost();
		for (int i = 0; i < 3; i++) {
			assertDoesNotThrow(() -> card.applyEffect(null, null, Color.BLUE, null));
			assertEquals(initialCost + 1, card.getCost());
		}
	}

	@Test
	void applyEffect_EmptyColorInDiningRoom_StillEmptyAndBagUnchanged() {
		final Color color = Color.BLUE;
		final int bagInitialQuantity = bag.getQuantity(color);
		assertDoesNotThrow(() -> card.applyEffect(null, null, color, null));
		assertEquals(0, testPlayer.getDiningRoom().getQuantity(color));
		assertEquals(bagInitialQuantity, bag.getQuantity(color));
	}

	@Test
	void applyEffect_OneStudentOfTargetColor_OneStudentReturnedToBag() {
		final Color color = Color.GREEN;
		final int bagInitialQuantity;
		assertDoesNotThrow(() -> bag.moveTo(testPlayer.getDiningRoom(), color));
		bagInitialQuantity = bag.getQuantity(color);
		assertDoesNotThrow(() -> card.applyEffect(null, null, color, null));
		assertEquals(0, testPlayer.getDiningRoom().getQuantity(color));
		assertEquals(bagInitialQuantity + 1, bag.getQuantity(color));
	}

	@Test
	void applyEffect_ThreeStudentsOfTargetColor_ThreeStudentsReturnedToBag() {
		final Color color = Color.RED;
		final int bagInitialQuantity;
		for (int i = 0; i < 3; i++) assertDoesNotThrow(() -> bag.moveTo(testPlayer.getDiningRoom(), color));
		bagInitialQuantity = bag.getQuantity(color);
		assertDoesNotThrow(() -> card.applyEffect(null, null, color, null));
		assertEquals(0, testPlayer.getDiningRoom().getQuantity(color));
		assertEquals(bagInitialQuantity + 3, bag.getQuantity(color));
	}

	@Test
	void applyEffect_FourStudentsOfTargetColor_ThreeStudentsReturnedToBag() {
		final Color color = Color.RED;
		final int bagInitialQuantity;
		for (int i = 0; i < 4; i++) assertDoesNotThrow(() -> bag.moveTo(testPlayer.getDiningRoom(), color));
		bagInitialQuantity = bag.getQuantity(color);
		assertDoesNotThrow(() -> card.applyEffect(null, null, color, null));
		assertEquals(1, testPlayer.getDiningRoom().getQuantity(color));
		assertEquals(bagInitialQuantity + 3, bag.getQuantity(color));
	}

	@Test
	void applyEffect_NullTargetColor_InvalidArgumentException() {
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
	}
}