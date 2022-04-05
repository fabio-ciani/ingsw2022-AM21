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
		Player p = new Player("p");

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

		i1.setController(new Player("p1"));
		i2.setController(new Player("p2"));

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
	void getId_ReturnValidString() {
		assertEquals("91", new IslandGroup("91").getId());
	}

	@Test
	void getController_ReturnValidPlayer() {
		Player p = new Player("p");
		IslandGroup island = new IslandGroup("02");

		island.setController(p);
		assertEquals(p, island.getController());
	}

	@Test
	void setController_PassNull_NoChange() {
		IslandGroup island = new IslandGroup("09");
		Player p = new Player("p");

		island.setController(p);
		island.setController(null);
		assertEquals(p, island.getController());
	}

	@Test
	void setController_PassPlayer_SetFromNull() {
		IslandGroup island = new IslandGroup("09");
		Player p1 = new Player("p1");

		assertNull(island.getController());
		island.setController(p1);
		assertEquals(p1, island.getController());
	}

	@Test
	void setController_PassPlayer_SetFromPrevious() {
		IslandGroup island = new IslandGroup("09");
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");

		island.setController(p1);
		assertEquals(p1, island.getController());
		island.setController(p2);
		assertEquals(p2, island.getController());
	}

	@Test
	void getTowers_SingleIsland_Return1() {
		assertEquals(1, new IslandGroup("00").getTowers());
	}

	@Test
	void getTowers_TwoIslands_Return2() throws IncompatibleControllersException {
		IslandGroup i1 = new IslandGroup("01");
		IslandGroup i2 = new IslandGroup("02");
		Player p = new Player("p");

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
}