package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.DuplicateNoEntryTileException;
import it.polimi.ingsw.eriantys.model.exceptions.IncompatibleControllersException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandGroupTest {

	@Test
	void merge_PassIslandsWithSameController_ReturnValidIsland() {
		IslandGroup i1 = new IslandGroup("01");
		IslandGroup i2 = new IslandGroup("02");
		Player p = new Player("p", 9, 6);

		i1.setController(p);
		i2.setController(p);

		IslandGroup res = assertDoesNotThrow(() -> IslandGroup.merge(i1, i2));

		assertEquals(p, res.getController());
		assertTrue(res.getComponents().containsAll(i1.getComponents()));
		assertTrue(res.getComponents().containsAll(i2.getComponents()));
		assertEquals("01-02", res.getId());
	}

	@Test
	void merge_PassIslandsWithDifferentControllers_ThrowException() {
		IslandGroup i1 = new IslandGroup("01");
		IslandGroup i2 = new IslandGroup("02");

		i1.setController(new Player("p1", 7, 8));
		i2.setController(new Player("p2", 7, 8));

		assertThrowsExactly(IncompatibleControllersException.class, () -> IslandGroup.merge(i1, i2));
	}

	@Test
	void merge_PassNull_ReturnNull() throws IncompatibleControllersException {
		IslandGroup island = new IslandGroup("03");

		assertNull(IslandGroup.merge(island, null));
		assertNull(IslandGroup.merge(null, island));
		assertNull(IslandGroup.merge(null, null));
	}

	@Test
	void getId_NormalConditions_ReturnValidString() {
		assertEquals("91", new IslandGroup("91").getId());
	}

	@Test
	void getController_NormalConditions_ReturnValidPlayer() {
		Player p = new Player("p", 9, 6);
		IslandGroup island = new IslandGroup("02");

		island.setController(p);
		assertEquals(p, island.getController());
	}

	@Test
	void setController_PassNull_NoChange() {
		IslandGroup island = new IslandGroup("09");
		Player p = new Player("p", 9, 6);

		island.setController(p);
		island.setController(null);
		assertEquals(p, island.getController());
	}

	@Test
	void setController_PassPlayer_SetFromNull() {
		IslandGroup island = new IslandGroup("09");
		Player p = new Player("p1", 9, 6);

		assertNull(island.getController());
		island.setController(p);
		assertEquals(p, island.getController());
	}

	@Test
	void setController_PassPlayer_SetFromPrevious() {
		IslandGroup island = new IslandGroup("09");
		Player p1 = new Player("p1", 7, 8);
		Player p2 = new Player("p2", 7, 8);

		island.setController(p1);
		assertEquals(p1, island.getController());
		island.setController(p2);
		assertEquals(p2, island.getController());
	}

	@Test
	void getTowers_SingleIslandNoController_Return0() {
		assertEquals(0, new IslandGroup("01").getTowers());
	}

	@Test
	void getTowers_SingleIsland_Return1() {
		IslandGroup i1 = new IslandGroup("01");
		i1.setController(new Player("p", 9, 6));
		assertEquals(1, i1.getTowers());
	}

	@Test
	void getTowers_TwoIslands_Return2() throws IncompatibleControllersException {
		IslandGroup i1 = new IslandGroup("01");
		IslandGroup i2 = new IslandGroup("02");
		Player p = new Player("p", 9, 6);

		i1.setController(p);
		i2.setController(p);

		IslandGroup res = IslandGroup.merge(i1, i2);
		assertEquals(2, res.getTowers());
	}

	@Test
	void putNoEntryTile_PassValidId_AddTile() throws DuplicateNoEntryTileException {
		IslandGroup island = new IslandGroup("02");
		island.putNoEntryTile(1);
		assertEquals(1, island.popNoEntryTile());
	}

	@Test
	void putNoEntryTile_PassDuplicateId_ThrowException() throws DuplicateNoEntryTileException {
		IslandGroup island = new IslandGroup("02");
		island.putNoEntryTile(3);
		assertThrowsExactly(DuplicateNoEntryTileException.class, () -> island.putNoEntryTile(3));
	}

	@Test
	void popNoEntryTile_NoTilesPlaced_ReturnNull() {
		assertNull(new IslandGroup("33").popNoEntryTile());
	}

	@Test
	void popNoEntryTile_SomeTilesPlaced_ReturnLastTile() throws DuplicateNoEntryTileException {
		IslandGroup island = new IslandGroup("02");
		island.putNoEntryTile(3);
		island.putNoEntryTile(4);
		assertEquals(4, island.popNoEntryTile());
		assertEquals(3, island.popNoEntryTile());
		assertNull(island.popNoEntryTile());
	}

	@Test
	void getSize_SingleIsland_NormalPostConditions() {
		IslandGroup isle = new IslandGroup("03");

		assertEquals(1, isle.getSize());
	}

	@Test
	void getSize_Aggregate_NormalPostConditions() throws IncompatibleControllersException {
		IslandGroup i1 = new IslandGroup("01");
		IslandGroup i2 = new IslandGroup("02");
		IslandGroup i3 = new IslandGroup("03");

		Player p = new Player("admin", 9, 6);

		i1.setController(p);
		i2.setController(p);
		i3.setController(p);

		IslandGroup aggregate = IslandGroup.merge(i1, i2);
		aggregate = IslandGroup.merge(aggregate, i3);

		assertEquals(3, aggregate.getSize());
	}

	@Test
	void getNoEntryTiles_NormalPostConditions() throws DuplicateNoEntryTileException {
		IslandGroup isle = new IslandGroup("03");

		assertEquals(0, isle.getNoEntryTiles());

		isle.putNoEntryTile(1);
		assertEquals(1, isle.getNoEntryTiles());

		// Note: the actual limitation of 4 no-entry tiles is coded in the HerbGranny class
		for (int i = 2; i <= 4; i++)
			isle.putNoEntryTile(i);
		assertEquals(4, isle.getNoEntryTiles());

		isle.popNoEntryTile();
		assertEquals(3, isle.getNoEntryTiles());
	}
}