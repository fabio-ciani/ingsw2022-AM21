package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

	@Test
	void getEntrance_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		assertNotNull(p.getEntrance());
	}

	@Test
	void getDiningRoom_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		assertNotNull(p.getDiningRoom());
	}

	@Test
	void setTowerColor_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		p.setTowerColor(TowerColor.BLACK);
		assertSame(p.getTowerColor(), TowerColor.BLACK);
	}

	@Test
	void setWizard_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		p.setWizard(Wizard.DESERT);
		assertSame(p.getWizard(), Wizard.DESERT);
	}

	@Test
	void GetTowerAndPutTower_NormalPostConditions() {
		Player Alice = new Player("Alice", 7, 8);
		Player Bob = new Player("Bob", 7, 8);

		Alice.deployTower();
		Bob.deployTower();
		Bob.deployTower();

		assertEquals(7, Alice.getTowerQuantity());
		assertEquals(6, Bob.getTowerQuantity());
	}

	@Test
	void GetTowerAndPutTower_BoundaryConditions() {
		Player Alice = new Player("Alice", 7, 8);
		Player Bob = new Player("Bob", 7, 8);

		assertFalse(Alice.returnTower());
		assertEquals(8, Alice.getTowerQuantity());

		for (int i = 0; i < 8; i++)
			assertTrue(Bob.deployTower());
		assertFalse(Bob.deployTower());
		assertEquals(0, Bob.getTowerQuantity());
	}

	@Test
	void setMotherNatureMovements_PositiveParameter_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		assertDoesNotThrow(() -> p.setMotherNatureMovements(3));
		assertSame(3, p.getMotherNatureMovements());
	}

	@Test
	void setMotherNatureMovements_NegativeParameter_ThrowException() {
		Player p = new Player("admin", 9, 6);

		assertThrowsExactly(InvalidArgumentException.class, () -> p.setMotherNatureMovements(-10));
	}

	@Test
	void playAssistantCard_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);
		List<AssistantCard> oldDeck = p.getDeck();

		p.playAssistantCard(AssistantCard.TURTLE);

		List<AssistantCard> newDeck = p.getDeck();

		assertEquals(9, p.getDeck().size());
		assertTrue(oldDeck.containsAll(newDeck));
	}

	@Test
	void updateCoins_NormalPostConditions() {
		Player p = new Player("admin", 9, 6);

		p.updateCoins(7);
		assertSame(p.getCoins(), 1 + 7);

		p.updateCoins(-5);
		assertSame(3, p.getCoins());
	}

	@Test
	void equals_NormalPostConditions() {
		Player p1 = new Player("Alice", 7, 8);
		Player p2 = new Player("Bob", 7, 8);

		assertNotEquals(p1, p2);

		// TODO: Should we add entranceSize and towerNumber to the calculation of hashCode() and equals()?
		Player p3 = new Player("Alice", 7, 8);	// Note: a player with same username, but different parameters, cannot exist in the app

		assertEquals(p1, p3);
	}
}