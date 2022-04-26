package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Board;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HerbGrannyTest {
	Board board;
	BaseCharacterCard card;

	final int MAX_ENTRY_TILES = 4;

	@BeforeEach
	void init() {
		board = new Board(2, 3);
		card = new HerbGranny(board);
	}

	@Test
	void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() throws InvalidArgumentException {
		int initialCost = card.getCost();
		card.setupEffect();
		for (int i = 0; i < 3; i++) {
			IslandGroup island = new IslandGroup("01");
			assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
			assertEquals(initialCost + 1, card.getCost());
		}
	}

	@Test
	void applyEffect_TargetIslandWithNoTiles_OneTileOnIsland() throws InvalidArgumentException {
		IslandGroup island = new IslandGroup("01");
		card.setupEffect();
		assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
		assertNotNull(island.popNoEntryTile());
		assertNull(island.popNoEntryTile());
	}

	@Test
	void applyEffect_TargetIslandWithNoTilesApplyMaxTimes_MaxTilesOnIsland() throws InvalidArgumentException {
		IslandGroup island = new IslandGroup("01");
		card.setupEffect();
		for (int i = 0; i < MAX_ENTRY_TILES; i++) {
			assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
		}
		for (int i = 1; i <= MAX_ENTRY_TILES; i++) {
			assertNotNull(island.popNoEntryTile());
		}
		assertNull(island.popNoEntryTile());
	}

	@Test
	void applyEffect_TargetIslandWithNoTilesApplyMoreThanMaxTimes_ItemNotAvailableException() throws InvalidArgumentException {
		IslandGroup island = new IslandGroup("01");
		card.setupEffect();
		for (int i = 0; i < MAX_ENTRY_TILES; i++) {
			assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
		}
		assertThrows(ItemNotAvailableException.class, () -> card.applyEffect(null, null, null, island));
	}

	@Test
	void applyEffect_NullTargetIsland_InvalidArgumentException() throws InvalidArgumentException {
		card.setupEffect();
		assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
	}

	@Test
	void applyEffect_TargetIslandWithTileWithSameId_DuplicateNoEntryTileException() throws InvalidArgumentException {
		IslandGroup island = new IslandGroup("01");
		card.setupEffect();
		assertDoesNotThrow(() -> island.putNoEntryTile(1));
		assertThrows(DuplicateNoEntryTileException.class, () -> card.applyEffect(null, null, null, island));
	}

	@Test
	void returnTile_SetupAndApplyThenBoardCallsReturnTile_TileReturned() throws InvalidArgumentException {
		final int tileId = 1; // id of the first tile popped
		IslandGroup island1 = new IslandGroup("01");
		IslandGroup island2 = new IslandGroup("02");
		card.setupEffect();
		assertDoesNotThrow(() -> card.applyEffect(null, null, null, island1));
		assertTrue(board.noEntryEnforced(island1));
		assertDoesNotThrow(() -> card.applyEffect(null, null, null, island2));
		assertEquals(tileId, island2.popNoEntryTile());
	}
}