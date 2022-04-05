package it.polimi.ingsw.eriantys.model;

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
	void getMotherNatureIsland_AfterSetup_ReturnValidIsland() {
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
	void setup_NormalPostConditions() throws IslandNotFoundException {
		Board board = new Board(2, 3);
		board.setup();

		Map<Color, Integer> colors = new HashMap<>();
		List<Integer> emptyIslands = new ArrayList<>();

		for (int i = 0; i < 12; i++) {
			IslandGroup temp = board.getIsland(String.format("%02d", i+1));
			boolean foundColor = false;

			for (Color color : Color.values())
				if (temp.getQuantity(color) > 0) {
					// check that each island contains at most a student of each color
					assertEquals(1, temp.getQuantity(color));

					// check that on each island there is only one color such that there are students of that color on the island
					assertFalse(foundColor);
					foundColor = true;

					colors.put(color, 1 + (colors.get(color) == null ? 0 : colors.get(color)));
				}

			if (!foundColor)
				emptyIslands.add(i);
		}

		// check that there are exactly 2 students of each color deployed on the islands
		for (Color color : Color.values())
			assertEquals(2, colors.get(color));

		// check that there are exactly 2 islands with no students on it
		assertEquals(2, emptyIslands.size());

		// check that one of the 2 empty islands has Mother Nature on it
		assertTrue(
					board.getMotherNatureIsland().equals(board.getIsland(String.format("%02d", 1 + emptyIslands.get(0))))
					|| board.getMotherNatureIsland().equals(board.getIsland(String.format("%02d", 1 + emptyIslands.get(1)))));

		// check that the 2 empty islands are exactly one opposite the other
		assertEquals((int) emptyIslands.get(0), (emptyIslands.get(1) + 6) % 12);
	}

	@Test
	void drawStudents() {
	}

	@Test
	void refillClouds() {
	}

	@Test
	void unifyIslands() {
	}
}