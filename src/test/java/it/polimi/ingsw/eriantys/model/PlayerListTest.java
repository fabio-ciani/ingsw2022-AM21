package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerListTest {
	Player Alice = new Player("Alice");
	Player Bob = new Player("Bob");
	Player Eve = new Player("Eve");

	List<String> players = new ArrayList<>();
	PlayerList l;

	@BeforeEach
	void init() {
		players.add(Alice.getNickname());
		players.add(Bob.getNickname());
		players.add(Eve.getNickname());

		l = new PlayerList(players);
	}

	@Test
	void setFirst_NicknameNotFound_NoChange() {
		l.setFirst(new Player("admin"));

		List<Player> turnOrder = l.getTurnOrder();

		assertEquals(Alice, turnOrder.get(0));
		assertEquals(Bob, turnOrder.get(1));
		assertEquals(Eve, turnOrder.get(2));
	}

	@Test
	void SetFirstAndGetTurnOrder() {
		l.setFirst(Eve);

		List<Player> turnOrder = l.getTurnOrder();

		assertEquals(Eve, turnOrder.get(0));
		assertEquals(Alice, turnOrder.get(1));
		assertEquals(Bob, turnOrder.get(2));
	}

	@Test
	void get_NicknameFound_NormalPostConditions() {
		Player ans = l.get("Eve");

		assertEquals(ans, Eve);
	}

	@Test
	void get_NicknameNotFound_ReturnNull() {
		Player ans = l.get("admin");

		assertNull(ans);
	}
}