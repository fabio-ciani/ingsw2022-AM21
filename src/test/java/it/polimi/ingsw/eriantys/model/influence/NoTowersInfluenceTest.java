package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoTowersInfluenceTest {
    @Test
    void NoTowersInfluence_PassNull_ThrowException() {
        InfluenceCalculator calc = new NoTowersInfluence();

        assertThrowsExactly(IllegalInfluenceStateException.class, () -> calc.calculate(new Player("Bob"), null, null));
    }

    @Test
    void NoTowersInfluence_ValidParameters_NormalPostConditions() {
    }
}