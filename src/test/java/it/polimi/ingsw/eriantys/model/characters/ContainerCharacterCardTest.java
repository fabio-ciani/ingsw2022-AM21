package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContainerCharacterCardTest {
    private final int size = 6;
    private final int initialCost = 3;
    private Bag bag;
    private ContainerCharacterCard card;

    @BeforeEach
    void init() {
        bag = new Bag();
        card = new ContainerCharacterCard(size, initialCost, bag) {
            @Override
            public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland) {
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

    @Test
    void setupEffect_BagNotNull_FullContainerAfterSetup() {
        assertDoesNotThrow(() -> card.setupEffect());
        int sum = 0;
        for (Color color : Color.values()) {
            sum += card.getQuantity(color);
        }
        assertEquals(sum, size);
    }
}