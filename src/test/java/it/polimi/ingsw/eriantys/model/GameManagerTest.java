package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
	Player Alice = new Player("Alice");
	Player Bob = new Player("Bob");
	Player Eve = new Player("Eve");

	List<String> players = new ArrayList<>();

	@BeforeEach
	void init() {
		players.add(Alice.getNickname());
		players.add(Bob.getNickname());
		players.add(Eve.getNickname());
	}

	@Test
	void setupPlayer_PassInvalidTowerColor_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.setupPlayer(Eve.getNickname(), "RED", "DESERT_WIZARD"));
	}

	@Test
	void setupPlayer_PassInvalidWizard_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.setupPlayer(Eve.getNickname(), "BLACK", "SPACE_WIZARD"));
	}

	@Test
	void setupPlayer_ValidParameters_NormalPostConditions() {
		GameManager gm = new GameManager(players, false);

		assertDoesNotThrow(() -> gm.setupPlayer(Eve.getNickname(), "BLACK", "DESERT_WIZARD"));
	}

	@Test
	void HandleAssistantCardsAndGetTurnOrder() {
		GameManager gm = new GameManager(players, false);

		Map<String, String> playedCards = new HashMap<>();

		playedCards.put(Alice.getNickname(), AssistantCard.FOX.toString());
		playedCards.put(Bob.getNickname(), AssistantCard.TURTLE.toString());
		playedCards.put(Eve.getNickname(), AssistantCard.CHEETAH.toString());

		gm.handleAssistantCards(playedCards);

		List<String> turnOrder = gm.getTurnOrder();

		assertEquals(turnOrder.get(0), Eve.getNickname());
		assertEquals(turnOrder.get(1), Alice.getNickname());
		assertEquals(turnOrder.get(2), Bob.getNickname());
	}

	@Test
	void changeInfluenceState_PassNull_ThrowException() {
		GameManager gm = new GameManager(players, false);

		assertThrowsExactly(InvalidArgumentException.class, () -> gm.changeInfluenceState(null));
	}
}