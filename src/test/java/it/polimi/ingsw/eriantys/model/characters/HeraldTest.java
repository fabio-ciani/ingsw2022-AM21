package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeraldTest {
    GameManager gameManager;
    BaseCharacterCard card;

    @BeforeEach
    void init() {
        gameManager = new GameManager(List.of("Nick", "Name"), true);
        card = new Herald(gameManager);
    }

    // TODO: 08/04/2022 Disabled test: error in CommonInfluence
    @Test
    @Disabled
    void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
        int initialCost = card.getCost();
        IslandGroup island = new IslandGroup("01"); // should be taken from the board
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
            assertEquals(initialCost + 1, card.getCost());
        }
    }

    // TODO: 08/04/2022 Disabled test: error in CommonInfluence
    @Test
    @Disabled
    void applyEffect_TargetIsland_NoExceptionThrown() {
        IslandGroup island = new IslandGroup("01");
        assertDoesNotThrow(() -> card.applyEffect(null, null, null, island));
    }

    @Test
    void applyEffect_NullTargetIsland_InvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
    }
}