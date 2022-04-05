package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorOwnershipTest {
	Player currentPlayer;

	@Test
	void getProfessors_PassNull_ReturnEmptySet() {
		assertEquals(0, new ProfessorOwnership(this::supplyPlayer).getProfessors(null).size());
	}

	@Test
	void getProfessors_PlayerWithNoProfessors_ReturnEmptySet() {
		assertEquals(0, new ProfessorOwnership(this::supplyPlayer).getProfessors(new Player("p1")).size());
	}

	@Test
	void getProfessors_PlayerWithSomeProfessors_ReturnNonEmptySet() throws NoMovementException {
		Player p1 = new Player("p1");
		this.currentPlayer = p1;
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);
		Bag bag = new Bag();

		bag.moveTo(p1.getDiningRoom(), Color.PINK);
		ownership.update(new HashSet<>(List.of(Color.PINK)));
		assertTrue(ownership.getProfessors(p1).contains(Color.PINK));
	}

	@Test
	void update_TieEffectActive_ChangeProfessorsWithWinOrTie() throws NoMovementException {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Bag bag = new Bag();
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		ownership.activateEffect();

		this.currentPlayer = p1;
		bag.moveTo(p1.getDiningRoom(), Color.BLUE);
		ownership.update(new HashSet<>(List.of(Color.BLUE)));
		assertTrue(ownership.getProfessors(p1).contains(Color.BLUE));

		this.currentPlayer = p2;
		bag.moveTo(p2.getDiningRoom(), Color.BLUE);
		ownership.update(new HashSet<>(List.of(Color.BLUE)));
		assertTrue(ownership.getProfessors(p2).contains(Color.BLUE));
		assertFalse(ownership.getProfessors(p1).contains(Color.BLUE));
	}

	@Test
	void update_TieEffectInactive_NoChangeProfessorsWithTie() throws NoMovementException {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Bag bag = new Bag();
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		ownership.activateEffect();
		ownership.deactivateEffect();

		this.currentPlayer = p1;
		bag.moveTo(p1.getDiningRoom(), Color.RED);
		ownership.update(new HashSet<>(List.of(Color.RED)));
		assertTrue(ownership.getProfessors(p1).contains(Color.RED));

		this.currentPlayer = p2;
		bag.moveTo(p2.getDiningRoom(), Color.RED);
		ownership.update(new HashSet<>(List.of(Color.RED)));
		assertTrue(ownership.getProfessors(p1).contains(Color.RED));
		assertFalse(ownership.getProfessors(p2).contains(Color.RED));
	}

	@Test
	void update_TieEffectActive_ChangeProfessorsWithWin() throws NoMovementException {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Bag bag = new Bag();
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		ownership.activateEffect();

		this.currentPlayer = p1;
		bag.moveTo(p1.getDiningRoom(), Color.YELLOW);
		ownership.update(new HashSet<>(List.of(Color.YELLOW)));
		assertTrue(ownership.getProfessors(p1).contains(Color.YELLOW));

		this.currentPlayer = p2;
		bag.moveTo(p2.getDiningRoom(), Color.YELLOW);
		bag.moveTo(p2.getDiningRoom(), Color.YELLOW);
		ownership.update(new HashSet<>(List.of(Color.YELLOW)));
		assertTrue(ownership.getProfessors(p2).contains(Color.YELLOW));
		assertFalse(ownership.getProfessors(p1).contains(Color.YELLOW));
	}

	@Test
	void update_TieEffectInactive_ChangeProfessorsWithWin() throws NoMovementException {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Bag bag = new Bag();
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		this.currentPlayer = p1;
		bag.moveTo(p1.getDiningRoom(), Color.YELLOW);
		ownership.update(new HashSet<>(List.of(Color.YELLOW)));
		assertTrue(ownership.getProfessors(p1).contains(Color.YELLOW));

		this.currentPlayer = p2;
		bag.moveTo(p2.getDiningRoom(), Color.YELLOW);
		bag.moveTo(p2.getDiningRoom(), Color.YELLOW);
		ownership.update(new HashSet<>(List.of(Color.YELLOW)));
		assertTrue(ownership.getProfessors(p2).contains(Color.YELLOW));
		assertFalse(ownership.getProfessors(p1).contains(Color.YELLOW));
	}

	@Test
	void update_TieEffectActive_NoChangeProfessorsWithTieAt0() {
		Player p1 = new Player("p1");
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		this.currentPlayer = p1;

		assertEquals(0, ownership.getProfessors(p1).size());
		ownership.activateEffect();
		ownership.update(Arrays.stream(Color.values()).collect(Collectors.toSet()));
		assertEquals(0, ownership.getProfessors(p1).size());
	}

	@Test
	void update_TieEffectInactive_NoChangeProfessorsWithTieAt0() {
		Player p1 = new Player("p1");
		ProfessorOwnership ownership = new ProfessorOwnership(this::supplyPlayer);

		this.currentPlayer = p1;

		assertEquals(0, ownership.getProfessors(p1).size());
		ownership.update(Arrays.stream(Color.values()).collect(Collectors.toSet()));
		assertEquals(0, ownership.getProfessors(p1).size());
	}

	Player supplyPlayer() {
		return currentPlayer;
	}
}