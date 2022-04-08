package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BaseCharacterCardTest {
    BaseCharacterCard card;

    final int initialCost = 1;

    @BeforeEach
    void init() {
        card = new BaseCharacterCard(initialCost) {
            @Override
            public void applyEffect(List<Color> sourceColors,
                                    List<Color> destinationColors,
                                    Color targetColor,
                                    IslandGroup targetIsland) {
                increaseCost();
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
}