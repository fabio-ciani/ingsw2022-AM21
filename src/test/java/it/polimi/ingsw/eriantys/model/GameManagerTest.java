package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
	Player Alice = new Player("Alice", 9, 6);
	Player Bob = new Player("Bob", 9, 6);
	Player Eve = new Player("Eve", 9, 6);

	List<String> players = new ArrayList<>();

	@BeforeEach
	void init() {
		players.add(Alice.getNickname());
		players.add(Bob.getNickname());
		players.add(Eve.getNickname());
	}

	// TODO: Add a test which tries to create a GameManager with a single player -> Gson exception

	@Test
	void setupPlayer_PassInvalidTowerColor_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.setupPlayer(Eve.getNickname(), "RED", "DESERT"));
	}

	@Test
	void setupPlayer_PassInvalidWizard_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.setupPlayer(Eve.getNickname(), "BLACK", "SPACE"));
	}

	@Test
	void setupPlayer_ValidParameters_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.setupPlayer(Eve.getNickname(), "BLACK", "DESERT"));
	}

	@Test
	void HandleAssistantCardsAndGetTurnOrder() {
		GameManager gm = new GameManager(players, false);

		Map<String, String> playedCards = new HashMap<>();

		playedCards.put(Alice.getNickname(), AssistantCard.FOX.toString());
		playedCards.put(Bob.getNickname(), AssistantCard.TURTLE.toString());
		playedCards.put(Eve.getNickname(), AssistantCard.CHEETAH.toString());

		gm.handleAssistantCards(playedCards);	// ignore return statement

		List<String> turnOrder = gm.getTurnOrder();

		assertEquals(Eve.getNickname(), turnOrder.get(0));
		assertEquals(Alice.getNickname(), turnOrder.get(1));
		assertEquals(Bob.getNickname(), turnOrder.get(2));
	}

	@Test
	void changeInfluenceState_PassNull_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.changeInfluenceState(null));
	}

	@Test
	void entranceRepresentation_UsernameNotFound_ThrowException() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(NullPointerException.class, () -> gm.entranceRepresentation("admin"));
	}

	@Test
	void diningRoomRepresentation_UsernameNotFound_ThrowException() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(NullPointerException.class, () -> gm.diningRoomRepresentation("admin"));
	}

	@Test
	void towersRepresentation_UsernameNotFound_NormalPostConditions() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(NullPointerException.class, () -> gm.towersRepresentation("admin"));
	}

	@Test
	void towersRepresentation_NoActions_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		for (String p : players)
			assertEquals(6, gm.towersRepresentation(p));
	}

	@Test
	void coinsRepresentation_UsernameNotFound_NormalPostConditions() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(NullPointerException.class, () -> gm.coinsRepresentation("admin"));
	}

	@Test
	void islandSizeRepresentation_PassInvalidIsle_ReturnNull() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.islandSizeRepresentation("13"));	// The throwing does not happen because of the catch clause
		assertNull(gm.islandSizeRepresentation("13"));
	}

	@Test
	void islandSizeRepresentation_AfterSetup_ReturnSingleIslandSize() {
		GameManager gm = new GameManager(players, false);

		assertEquals(1, gm.islandSizeRepresentation("03"));
	}

	@Test
	void islandStudentsRepresentation_PassInvalidIsle_ReturnNull() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.islandStudentsRepresentation("13"));	// The throwing does not happen because of the catch clause
		assertNull(gm.islandStudentsRepresentation("13"));
	}

	@Test
	void islandControllerRepresentation_PassInvalidIsle_ReturnNull() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.islandControllerRepresentation("13"));	// The throwing does not happen because of the catch clause
		assertNull(gm.islandControllerRepresentation("13"));
	}

	@Test
	void islandControllerRepresentation_NoController_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		assertNull(gm.islandControllerRepresentation("03"));
	}

	@Test
	void motherNatureIslandRepresentation_BeforeSetup_ThrowException() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(NullPointerException.class, () -> gm.motherNatureIslandRepresentation());
	}

	@Test
	void motherNatureIslandRepresentation_AfterSetup_NormalPostConditions() throws InvalidArgumentException, NoMovementException {
		GameManager gm = new GameManager(players, false);

		gm.setupBoard();

		assertNotNull(gm.motherNatureIslandRepresentation());
	}

	@Test
	void islandNoEntryTilesRepresentation_PassInvalidIsle_ReturnNull() {
		// Note: the test case will never happen
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.islandNoEntryTilesRepresentation("13"));	// The throwing does not happen because of the catch clause
		assertNull(gm.islandNoEntryTilesRepresentation("13"));
	}

	@Test
	void islandNoEntryTilesRepresentation_AfterSetup_ReturnZero() {
		GameManager gm = new GameManager(players, false);

		assertEquals(0, gm.islandNoEntryTilesRepresentation("03"));
	}

	@Test
	void cloudTilesRepresentation_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		assertEquals(3, gm.cloudTilesRepresentation().keySet().size());
	}

	@Test
	void professorsRepresentation_AfterSetup_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		Map<String, String> rep = gm.professorsRepresentation();

		assertEquals(5, rep.keySet().size());
		for (String c : rep.keySet())
			assertNull(rep.get(c));
	}

	@Test
	void charactersRepresentation_SimplifiedMode_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		assertNull(gm.charactersRepresentation());
	}

	@Test
	void charactersRepresentation_ExpertMode_NormalPostConditions() {
		GameManager gm = new GameManager(players, true);

		List<String> rep = gm.charactersRepresentation();

		assertEquals(3, rep.size());
		for (String c : rep)
			assertNotNull(c);
	}
}