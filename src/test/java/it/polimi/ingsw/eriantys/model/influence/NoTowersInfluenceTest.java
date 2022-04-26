package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NoTowersInfluenceTest {
	InfluenceCalculator calc = new NoTowersInfluence();

	@Test
	void NoTowersInfluence_PassNull_ThrowException() {
		assertThrowsExactly(InvalidArgumentException.class, () -> calc.calculate(new Player("Bob"), null, null));
	}

	@Test
	void NoTowersInfluence_ValidParameters_NormalPostConditions() throws InvalidArgumentException, NoMovementException {
		Player p = new Player("admin");
		IslandGroup island = new IslandGroup("03");
		Set<Color> professors = new HashSet<>();

		island.setController(p);
		professors.add(Color.PINK);
		professors.add(Color.BLUE);

		Bag b = new Bag();

		b.moveTo(island, Color.PINK);
		b.moveTo(island, Color.RED);

		assertEquals(1, calc.calculate(p, island, professors));
	}
}