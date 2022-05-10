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

class ExcludeColorInfluenceTest {
	InfluenceCalculator calc = new ExcludeColorInfluence(Color.PINK);

	@Test
	void ExcludeColorInfluence_PassNull_ThrowException() {
		assertThrowsExactly(InvalidArgumentException.class, () -> calc.calculate(new Player("Eve", 9, 6), null, null));
	}

	@Test
	void ExcludeColorInfluence_ValidParameters_NormalPostConditions() throws InvalidArgumentException, NoMovementException {
		Player p = new Player("admin", 9, 6);
		IslandGroup island = new IslandGroup("03");
		Set<Color> professors = new HashSet<>();

		island.setController(new Player("hacker", 9, 6));
		professors.add(Color.PINK);
		professors.add(Color.BLUE);

		Bag b = new Bag();

		b.moveTo(island, Color.PINK);
		b.moveTo(island, Color.RED);

		assertEquals(0, calc.calculate(p, island, professors));
	}
}