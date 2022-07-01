package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.IslandNotFoundException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

	@Test
	void getIsland_PassNull_ThrowException() {
		assertThrowsExactly(IslandNotFoundException.class, () -> new Board(2, 3).getIsland(null));
	}

	@Test
	void getIsland_PassEmptyString_ThrowException() {
		assertThrowsExactly(IslandNotFoundException.class, () -> new Board(2, 3).getIsland(""));
	}

	@Test
	void getIsland_PassValidId_ReturnIsland() throws IslandNotFoundException {
		Board board = new Board(2, 3);
		IslandGroup res = board.getIsland("02");
		assertEquals("02", res.getId());
	}

	@Test
	void getIsland_PassInvalidIdLow_ThrowException() {
		assertThrowsExactly(IslandNotFoundException.class, () -> new Board(2, 3).getIsland("00"));
	}

	@Test
	void getIsland_PassInvalidIdHigh_ThrowException() {
		assertThrowsExactly(IslandNotFoundException.class, () -> new Board(2, 3).getIsland("13"));
	}

	@Test
	void getMotherNatureIsland_BeforeSetup_ThrowException() {
		assertNull(new Board(2, 3).getMotherNatureIsland());
	}

	@Test
	void getMotherNatureIsland_AfterSetup_ReturnValidIsland() throws InvalidArgumentException, NoMovementException {
		Board board = new Board(2, 3);
		board.setup();
		IslandGroup res = assertDoesNotThrow(board::getMotherNatureIsland);
		assertDoesNotThrow(() -> board.getIsland(res.getId()));
	}

	@Test
	void getMotherNatureIsland_NotDeployed_ReturnNull() {
		assertNull(new Board(2, 3).getMotherNatureIsland());
	}

	@Test
	void getMotherNatureIsland_DeployedIsland01_ReturnIsland01() throws IslandNotFoundException {
		Board board = new Board(2, 3);
		IslandGroup dest = board.getIsland("01");
		board.moveMotherNature(dest);
		assertEquals(dest, board.getMotherNatureIsland());
	}

	@Test
	void setup_NormalConditions_IslandsSetUpCorrectly()
				throws IslandNotFoundException, InvalidArgumentException, NoMovementException {
		Board board = new Board(2, 3);
		board.setup();

		Map<Color, Integer> colors = new HashMap<>();
		List<Integer> emptyIslands = new ArrayList<>();

		for (int i = 0; i < 12; i++) {
			IslandGroup temp = board.getIsland(String.format("%02d", i+1));
			boolean foundColor = false;

			for (Color color : Color.values())
				if (temp.getQuantity(color) > 0) {
					// Check that each island contains at most a student of each color
					assertEquals(1, temp.getQuantity(color));

					// Check that on each island there is only one color such that there are students of that color on the island
					assertFalse(foundColor);
					foundColor = true;

					colors.put(color, 1 + (colors.get(color) == null ? 0 : colors.get(color)));
				}

			if (!foundColor)
				emptyIslands.add(i);
		}

		// Check that there are exactly 2 students of each color deployed on the islands
		for (Color color : Color.values())
			assertEquals(2, colors.get(color));

		// Check that there are exactly 2 islands with no students on it
		assertEquals(2, emptyIslands.size());

		// Check that one of the 2 empty islands has Mother Nature on it
		assertTrue(
					board.getMotherNatureIsland().equals(board.getIsland(String.format("%02d", 1 + emptyIslands.get(0))))
					|| board.getMotherNatureIsland().equals(board.getIsland(String.format("%02d", 1 + emptyIslands.get(1)))));

		// Check that the 2 empty islands are exactly one opposite the other
		assertEquals((int) emptyIslands.get(0), (emptyIslands.get(1) + 6) % 12);
	}

	@Test
	void drawStudents_NullPlayer_ThrowException() {
		assertThrowsExactly(InvalidArgumentException.class,
					() -> new Board(2, 3).drawStudents(0, null));
	}

	@Test
	void drawStudents_IndexOutOfBounds_ThrowException() {
		Player p = new Player("p", 9, 6);
		assertThrowsExactly(InvalidArgumentException.class,
					() -> new Board(2, 3).drawStudents(-1, p));
		assertThrowsExactly(InvalidArgumentException.class,
					() -> new Board(2, 3).drawStudents(2, p));
	}

	@Test
	void drawStudents_EmptyCloud_ThrowException() {
		Board board = new Board(2, 3);
		Player p = new Player("p", 9, 6);
		assertThrowsExactly(NoMovementException.class, () -> board.drawStudents(0, p));
	}

	@Test
	void drawStudents_IndexAndPlayerOk_MoveStudents() throws InvalidArgumentException, NoMovementException {
		Board board = new Board(2, 3);
		Player p = new Player("p", 9, 6);
		board.refillClouds();
		board.drawStudents(0, p);
		assertEquals(3,
					Arrays.stream(Color.values()).mapToInt(p.getEntrance()::getQuantity).reduce(0, Integer::sum));
	}

	@Test
	void moveMotherNature_NonexistentIsland_ReturnFalse() {
		Board board = new Board(2, 3);
		assertFalse(board.moveMotherNature(new IslandGroup("99")));
	}

	@Test
	void moveMotherNature_ExistingIsland_ReturnTrueAndMoveMotherNature() throws IslandNotFoundException {
		Board board = new Board(2, 3);
		IslandGroup dest = board.getIsland("02");
		assertTrue(board.moveMotherNature(dest));
		assertEquals(dest, board.getMotherNatureIsland());
	}

	@Test
	void refillClouds_EmptyClouds_FillClouds() throws InvalidArgumentException, NoMovementException {
		Board board = new Board(2, 3);
		Player p = new Player("p", 9, 6);
		board.refillClouds();
		assertDoesNotThrow(() -> board.drawStudents(0, p));
		assertDoesNotThrow(() -> board.drawStudents(1, p));
	}

	@Test
	void unifyIslands_NullTarget_ThrowException() {
		assertThrowsExactly(InvalidArgumentException.class,
					() -> new Board(2, 3).unifyIslands(null));
	}

	@Test
	void unifyIslands_NonexistentTarget_ThrowException() {
		assertThrowsExactly(IslandNotFoundException.class,
					() -> new Board(2, 3).unifyIslands(new IslandGroup("89")));
	}

	@Test
	void unifyIslands_DifferentControllers_NoEffect() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();	// Needed due to MN index retrieval
		Player p1 = new Player("p1", 7, 8);
		Player p2 = new Player("p2", 7, 8);

		board.getIsland("01").setController(p1);
		board.getIsland("02").setController(p2);
		board.getIsland("03").setController(p1);

		board.unifyIslands(board.getIsland("02"));

		assertDoesNotThrow(() -> board.getIsland("01"));
		assertDoesNotThrow(() -> board.getIsland("02"));
		assertDoesNotThrow(() -> board.getIsland("03"));
	}

	@Test
	void unifyIslands_TargetAndPrevSameController_MergeTargetAndPrev() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();	// Needed due to MN index retrieval
		Player p1 = new Player("p1", 7, 8);
		Player p2 = new Player("p2", 7, 8);

		board.getIsland("01").setController(p1);
		board.getIsland("02").setController(p1);
		board.getIsland("03").setController(p2);

		board.unifyIslands(board.getIsland("02"));

		assertDoesNotThrow(() -> board.getIsland("03"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("01"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("02"));

		assertDoesNotThrow(() -> board.getIsland("01-02"));
	}

	@Test
	void unifyIslands_TargetAndNextSameController_MergeTargetAndNext() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();	// Needed due to MN index retrieval
		Player p1 = new Player("p1", 7, 8);
		Player p2 = new Player("p2", 7, 8);

		board.getIsland("01").setController(p2);
		board.getIsland("02").setController(p1);
		board.getIsland("03").setController(p1);

		board.unifyIslands(board.getIsland("02"));

		assertDoesNotThrow(() -> board.getIsland("01"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("02"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("03"));

		assertDoesNotThrow(() -> board.getIsland("02-03"));
	}

	@Test
	void unifyIslands_TargetPrevAndNextSameController_MergeTargetPrevAndNext() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();	// Needed due to MN index retrieval
		Player p = new Player("p", 9, 6);

		board.getIsland("01").setController(p);
		board.getIsland("02").setController(p);
		board.getIsland("03").setController(p);

		board.unifyIslands(board.getIsland("02"));

		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("01"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("02"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("03"));

		assertDoesNotThrow(() -> board.getIsland("01-02-03"));
	}

	@Test
	void unifyIslands_MoreThanTwoIslandsSameController_MergeOnlyTwoIslands() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();	// Needed due to MN index retrieval
		Player p = new Player("p", 9, 6);

		board.getIsland("01").setController(p);
		board.getIsland("02").setController(p);
		board.getIsland("03").setController(p);

		board.unifyIslands(board.getIsland("03"));	// The method does not recursively call unifyIslands(), hence only "02" and "03" islands are merged

		assertEquals(11, board.getIslandNumber());
		assertDoesNotThrow(() -> board.getIsland("02-03"));
		assertThrowsExactly(IslandNotFoundException.class, () -> board.getIsland("01-02-03"));
	}

	@Test
	void getDistanceFromMotherNature_NullTarget_ReturnNeg1() {
		Board board = new Board(2, 3);
		assertDoesNotThrow(board::setup);
		assertEquals(-1, board.getDistanceFromMotherNature(null));
	}

	@Test
	void getDistanceFromMotherNature_NonexistentTarget_ReturnNeg1() {
		Board board = new Board(2, 3);
		assertDoesNotThrow(board::setup);
		assertEquals(-1, board.getDistanceFromMotherNature(new IslandGroup("98")));
	}

	@Test
	void getDistanceFromMotherNature_3StepsAhead_Return3() {
		Board board = new Board(2, 3);
		assertDoesNotThrow(board::setup);
		assertTrue(board.moveMotherNature(new IslandGroup("01")));
		assertEquals(3, board.getDistanceFromMotherNature(new IslandGroup("04")));
	}

	@Test
	void getDistanceFromMotherNature_StartIndexGreaterThanEndIndexBy3_Return9() {
		Board board = new Board(2, 3);
		assertDoesNotThrow(board::setup);
		assertTrue(board.moveMotherNature(new IslandGroup("09")));
		assertEquals(9, board.getDistanceFromMotherNature(new IslandGroup("06")));
	}

	@Test
	void getIslandsRepresentation_NoAggregates_NormalPostConditions() throws InvalidArgumentException, NoMovementException {
		Board b = new Board(2, 3);
		b.setup();	// Needed due to MN index retrieval

		List<String> rep = b.getIslandsRepresentation();

		assertEquals(12, b.getIslandNumber());
		assertEquals(b.getIslandNumber(), rep.size());
		for (String isle : rep)
			assertDoesNotThrow(() -> b.getIsland(isle));
	}

	@Test
	void getIslandsRepresentation_WithAggregates_NormalPostConditions() throws InvalidArgumentException, NoMovementException, IslandNotFoundException {
		Board b = new Board(2, 3);
		b.setup();	// Needed due to MN index retrieval
		Player p = new Player("p", 9, 6);

		b.getIsland("01").setController(p);
		b.getIsland("02").setController(p);
		b.getIsland("03").setController(p);

		b.unifyIslands(b.getIsland("02"));

		List<String> rep = b.getIslandsRepresentation();

		assertEquals(10, b.getIslandNumber());
		assertEquals(b.getIslandNumber(), rep.size());
		for (String isle : rep)
			assertDoesNotThrow(() -> b.getIsland(isle));
	}

	@Test
	void getCloudTilesRepresentation_NormalPostConditions() throws InvalidArgumentException, NoMovementException {
		Board b = new Board(2, 3);
		b.setup();	// Needed due to MN index retrieval

		Map<String, Map<String, Integer>> rep = b.getCloudTiles();

		assertEquals(2, rep.keySet().size());
		// TODO: Check if every cloud has three students? (at the moment, this is impossible due to lack of getters)
	}
}