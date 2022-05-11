package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {
	GameManager gameManager;
	InfluenceCharacterCard card;

	@BeforeEach
	void init() {
		gameManager = new GameManager(List.of("Nick", "Name"), true);
		Player player = new Player("Nick", 9, 6);
		card = new Knight(gameManager, () -> player);
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
	void applyEffect_NoArguments_NoExceptionThrown() {
		assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
	}

	@Test
	void getName_NormalPostConditions() {
		assertEquals("Knight", card.getName());
	}
}