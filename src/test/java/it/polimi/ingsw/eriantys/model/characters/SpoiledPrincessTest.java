package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpoiledPrincessTest {
    Bag bag;
    Player player;
    ContainerCharacterCard card;

    final int DINING_ROOM_MAX_PER_COLOR = 10;

    @BeforeEach
    void init() {
        bag = new Bag();
        player = new Player("Nick Name");
        card = new SpoiledPrincess(bag, () -> player);
    }

    @Test
    void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
        Color student = Color.PINK;
        int initialCost = card.getCost();
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> {
                bag.moveTo(card, student);
                card.applyEffect(null, null, student, null);
            });
            assertEquals(initialCost + 1, card.getCost());
        }
    }

    @Test
    void applyEffect_TargetStudent_StudentMoved() {
        Color student = Color.GREEN;
        assertDoesNotThrow(() -> {
            bag.moveTo(card, student);
            card.applyEffect(null, null, student, null);
        });
        int totalOnCard = 0;
        for (Color color : Color.values()) {
            totalOnCard += card.getQuantity(color);
            assertEquals(color.equals(student) ? 1 : 0, player.getDiningRoom().getQuantity(color));
        }
        assertEquals(1, totalOnCard);
    }

    @Test
    void applyEffect_NullTargetStudent_InvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
    }

    @Test
    void applyEffect_StudentNotOnCard_NoMovementException() {
        Color student = Color.YELLOW;
        assertThrows(NoMovementException.class, () -> card.applyEffect(null, null, student, null));
        assertDoesNotThrow(() -> bag.moveTo(card, Color.BLUE));
        assertThrows(NoMovementException.class, () -> card.applyEffect(null, null, student, null));
    }

    @Test
    void applyEffect_FullDiningRoom_NoMovementException() {
        Color student = Color.BLUE;
        assertDoesNotThrow(() -> {
            for (int i = 0; i < DINING_ROOM_MAX_PER_COLOR; i++) {
                bag.moveTo(player.getDiningRoom(), student);
            }
        });
        assertThrows(NoMovementException.class, () -> card.applyEffect(null, null, student, null));
    }
}