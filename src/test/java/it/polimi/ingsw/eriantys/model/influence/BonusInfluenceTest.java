package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BonusInfluenceTest {
    @Test
    void BonusInfluence_PassNull_ThrowException() {
        InfluenceCalculator calc = new BonusInfluence(new Player("admin"));

        assertThrowsExactly(IllegalInfluenceStateException.class, () -> calc.calculate(new Player("Eve"), null, null));
    }

    @Test
    void BonusInfluence_ValidParameters_NormalPostConditions() {
    }
}