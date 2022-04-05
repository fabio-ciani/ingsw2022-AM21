package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonInfluenceTest {
    @Test
    void CommonInfluence_PassNull_ThrowException() {
        InfluenceCalculator calc = new CommonInfluence();

        assertThrowsExactly(IllegalInfluenceStateException.class, () -> calc.calculate(new Player("Alice"), null, null));
    }

    @Test
    void CommonInfluence_ValidParameters_NormalPostConditions() {
    }
}