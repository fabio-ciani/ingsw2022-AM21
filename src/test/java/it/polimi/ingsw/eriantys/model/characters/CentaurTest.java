package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CentaurTest {
    GameManager gameManager;
    InfluenceCharacterCard card;

    @BeforeEach
    void init() {
        gameManager = new GameManager(List.of("Nick", "Name"), true);
        card = new Centaur(gameManager);
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