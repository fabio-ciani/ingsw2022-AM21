package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagicPostmanTest {
	Player player;
	BaseCharacterCard card;

	final int BONUS_MOVEMENTS = 2;

	@BeforeEach
	void init() {
		player = new Player("Nick Name", 9, 6);
		card = new MagicPostman(() -> player);
	}

	@Test
	void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
		int initialCost = card.getCost();
		for (int i = 0; i < 3; i++) {
			assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
			assertEquals(initialCost + 1, card.getCost());
		}
	}

	@Test
	void applyEffect_NoArguments_IncreasedMotherNatureMovements() {
		int initialMovements = player.getMotherNatureMovements();
		assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
		assertEquals(initialMovements + BONUS_MOVEMENTS, player.getMotherNatureMovements());
	}

	@Test
	void getName_NormalPostConditions() {
		assertEquals("MagicPostman", card.getName());
	}
}