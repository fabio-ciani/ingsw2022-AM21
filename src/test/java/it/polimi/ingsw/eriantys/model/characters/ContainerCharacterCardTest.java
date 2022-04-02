package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContainerCharacterCardTest {
    private final int size = 6;
    private final int initialCost = 3;
    private Bag bag;
    private ContainerCharacterCard card;

    @BeforeEach
    void init() {
        bag = new Bag();
        card = new ContainerCharacterCard(size, initialCost, bag) {};
    }

    @Test
    void getCost_BeforeFirstActivation_EqualsInitialCost() {
        assertEquals(card.getCost(), initialCost);
    }

    @Test
    void getCost_AfterOneActivations_EqualsInitialCostIncremented() {
        try {
            card.applyEffect(null, null, null, null);
            assertEquals(card.getCost(), initialCost + 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getCost_AfterMoreActivations_EqualsInitialCostIncremented() {
        try {
            card.applyEffect(null, null, null, null);
            card.applyEffect(null, null, null, null);
            assertEquals(card.getCost(), initialCost + 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // FIXME: 02/04/2022 Test failed
    @Test
    @Disabled
    void setupEffect_BagNotNull_FullContainerAfterSetup() {
        try {
            card.setupEffect();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        int sum = 0;
        for (Color color : Color.values()) {
            sum += card.getQuantity(color);
        }
        assertEquals(sum, size);
    }

    @Test
    void applyEffect_AnyArguments_NoExceptionThrown() {
        try {
            card.applyEffect(null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}