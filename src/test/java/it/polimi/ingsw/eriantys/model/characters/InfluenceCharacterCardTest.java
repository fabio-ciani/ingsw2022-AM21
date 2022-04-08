package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InfluenceCharacterCardTest {
    GameManager gameManager;
    InfluenceCharacterCard card;

    final int initialCost = 2;

    @BeforeEach
    void init() {
        gameManager = new GameManager(List.of("Nick", "Name"), true);
        card = new InfluenceCharacterCard(initialCost, gameManager) {
            @Override
            public void applyEffect(List<Color> sourceColors,
                                    List<Color> destinationColors,
                                    Color targetColor,
                                    IslandGroup targetIsland) throws InvalidArgumentException {
                effectInfluenceCalculator = (player, island, ownedProfessors) -> 0;
                super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
            }
        };
    }

    @Test
    void getCost_BeforeFirstActivation_EqualsInitialCost() {
        assertEquals(card.getCost(), initialCost);
    }

    @Test
    void getCost_AfterOneActivations_EqualsInitialCostIncremented() {
        assertDoesNotThrow(() -> card.applyEffect(null, null, null, null));
        assertEquals(card.getCost(), initialCost + 1);
    }

    @Test
    void getCost_AfterMoreActivations_EqualsInitialCostIncremented() {
        assertDoesNotThrow(() -> {
            card.applyEffect(null, null, null, null);
            card.applyEffect(null, null, null, null);
        });
        assertEquals(card.getCost(), initialCost + 1);
    }

    @Test
    void cancelEffect_NormalInfluenceCalculatorNotNull_NoExceptionThrown() {
        assertDoesNotThrow(() -> card.cancelEffect());
    }
}