package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.ProfessorOwnership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FarmerTest {
    ProfessorOwnership professorOwnership;
    BaseCharacterCard card;

    @BeforeEach
    void init() {
        Player player = new Player("Nick Name");
        professorOwnership = new ProfessorOwnership(() -> player);
        card = new Farmer(professorOwnership);
    }

    @Test
    void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
        int initialCost = card.getCost();
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
            assertEquals(initialCost + 1, card.getCost());
        }
    }

    @Test
    void applyEffect_NoArguments_NoExceptionThrown() {
        assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
    }
}