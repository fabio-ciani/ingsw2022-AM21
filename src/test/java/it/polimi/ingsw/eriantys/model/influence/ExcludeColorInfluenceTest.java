package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcludeColorInfluenceTest {
    @Test
    void ExcludeColorInfluence_PassNull_ThrowException() {
        InfluenceCalculator calc = new ExcludeColorInfluence(Color.PINK);

        assertThrowsExactly(IllegalInfluenceStateException.class, () -> calc.calculate(new Player("Eve"), null, null));
    }

    @Test
    void ExcludeColorInfluence_ValidParameters_NormalPostConditions() {
    }
}