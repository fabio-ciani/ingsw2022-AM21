package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JesterTest {
	Bag bag;
	Player player;
	ContainerCharacterCard card;

	@BeforeEach
	void init() {
		bag = new Bag();
		player = new Player("Nick Name");
		card = new Jester(bag, () -> player);
	}

	@Test
	void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
		List<Color> src = List.of(Color.BLUE);
		List<Color> dst = List.of(Color.RED);
		int initialCost = card.getCost();
		for (int i = 0; i < 3; i++) {
			assertDoesNotThrow(() -> {
				bag.moveTo(card, Color.BLUE);
				bag.moveTo(player.getEntrance(), Color.RED);
				card.applyEffect(src, dst, null, null);
			});
			assertEquals(initialCost + 1, card.getCost());
		}
	}

	@Test
	void applyEffect_OnePairOfStudentsToSwap_StudentsSwapped() {
		List<Color> src = List.of(Color.BLUE);
		List<Color> dst = List.of(Color.RED);
		assertDoesNotThrow(() -> {
			bag.moveTo(card, Color.BLUE);
			bag.moveTo(player.getEntrance(), Color.RED);
			card.applyEffect(src, dst, null, null);
		});
		for (Color color : Color.values()) {
			assertEquals(color.equals(Color.RED) ? 1 : 0, card.getQuantity(color));
			assertEquals(color.equals(Color.BLUE) ? 1 : 0, player.getEntrance().getQuantity(color));
		}
	}

	@Test
	void applyEffect_ThreePairsOfStudentsToSwap_StudentsSwapped() {
		List<Color> src = List.of(Color.BLUE, Color.BLUE, Color.YELLOW);
		List<Color> dst = List.of(Color.RED, Color.GREEN, Color.PINK);
		assertDoesNotThrow(() -> {
			bag.moveTo(card, Color.BLUE);
			bag.moveTo(card, Color.BLUE);
			bag.moveTo(card, Color.YELLOW);
			bag.moveTo(player.getEntrance(), Color.RED);
			bag.moveTo(player.getEntrance(), Color.GREEN);
			bag.moveTo(player.getEntrance(), Color.PINK);
			card.applyEffect(src, dst, null, null);
		});
		for (Color color : Color.values()) {
			int inCard = card.getQuantity(color);
			int inEntrance = player.getEntrance().getQuantity(color);
			switch (color) {
				case RED, GREEN, PINK -> {
					assertEquals(1, inCard);
					assertEquals(0, inEntrance);
				}
				case BLUE -> {
					assertEquals(0, inCard);
					assertEquals(2, inEntrance);
				}
				case YELLOW -> {
					assertEquals(0, inCard);
					assertEquals(1, inEntrance);
				}
			}
		}
	}

	@Test
	void applyEffect_FourPairsOfStudentsToSwap_InvalidArgumentException() {
		List<Color> src = List.of(Color.BLUE, Color.BLUE, Color.YELLOW, Color.YELLOW);
		List<Color> dst = List.of(Color.RED, Color.GREEN, Color.PINK, Color.PINK);
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
	}

	@Test
	void applyEffect_NullSourceOrDestination_InvalidArgumentException() {
		List<Color> src = List.of(Color.BLUE);
		List<Color> dst = List.of(Color.RED);
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, dst, null, null));
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, null, null, null));
	}

	@Test
	void applyEffect_DifferentSizeSourceAndDestination_InvalidArgumentException() {
		List<Color> src = List.of(Color.BLUE, Color.YELLOW);
		List<Color> dst = List.of(Color.RED, Color.GREEN, Color.PINK);
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
	}

	@Test
	void applyEffect_EmptySourceAndDestination_InvalidArgumentException() {
		List<Color> src = List.of();
		List<Color> dst = List.of();
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
	}

	@Test
	void applyEffect_StudentNotOnCard_NoMovementException() {
		List<Color> src = List.of(Color.BLUE);
		List<Color> dst = List.of(Color.RED);
		assertDoesNotThrow(() -> {
			bag.moveTo(card, Color.YELLOW);
			bag.moveTo(card, Color.RED);
			bag.moveTo(player.getEntrance(), Color.BLUE);
		});
		assertThrows(NoMovementException.class, () -> card.applyEffect(src, dst, null, null));
	}
}