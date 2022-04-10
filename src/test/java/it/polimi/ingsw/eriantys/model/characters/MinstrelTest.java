package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinstrelTest {
    Player player;
    BaseCharacterCard card;

    @BeforeEach
    void init() {
        player = new Player("Nick Name");
        card = new Minstrel(() -> player);
    }

    @Test
    void getCost_effectAppliedThreeTimes_CostIncreasedOnlyTheFirstTime() {
        StudentContainer entrance = player.getEntrance();
        StudentContainer diningRoom = player.getDiningRoom();
        Bag bag = new Bag();
        assertDoesNotThrow(() -> {
            bag.moveTo(entrance, Color.GREEN);
            bag.moveTo(diningRoom, Color.GREEN);
        });
        final int initialCost = card.getCost();
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> card.applyEffect(List.of(Color.GREEN), List.of(Color.GREEN), null, null));
            assertEquals(initialCost + 1, card.getCost());
        }
    }

    @Test
    void applyEffect_OnePairOfStudentsToSwap_StudentsSwapped() {
        StudentContainer entrance = player.getEntrance();
        StudentContainer diningRoom = player.getDiningRoom();
        Bag bag = new Bag();
        List<Color> src = List.of(Color.BLUE);
        List<Color> dst = List.of(Color.RED);
        assertDoesNotThrow(() -> {
            bag.moveTo(entrance, Color.BLUE);
            bag.moveTo(diningRoom, Color.RED);
            card.applyEffect(src, dst, null, null);
        });
        for (Color color : Color.values()) {
            assertEquals(color.equals(Color.RED) ? 1 : 0, entrance.getQuantity(color));
            assertEquals(color.equals(Color.BLUE) ? 1 : 0, diningRoom.getQuantity(color));
        }
    }

    @Test
    void applyEffect_TwoPairsOfStudentsToSwap_StudentsSwapped() {
        StudentContainer entrance = player.getEntrance();
        StudentContainer diningRoom = player.getDiningRoom();
        Bag bag = new Bag();
        List<Color> src = List.of(Color.BLUE, Color.YELLOW);
        List<Color> dst = List.of(Color.GREEN, Color.PINK);
        assertDoesNotThrow(() -> {
            bag.moveTo(entrance, Color.BLUE);
            bag.moveTo(entrance, Color.YELLOW);
            bag.moveTo(diningRoom, Color.GREEN);
            bag.moveTo(diningRoom, Color.PINK);
            card.applyEffect(src, dst, null, null);
        });
        for (Color color : Color.values()) {
            int inEntrance = entrance.getQuantity(color);
            int inDiningRoom = diningRoom.getQuantity(color);
            switch (color) {
                case RED -> {
                    assertEquals(0, inEntrance);
                    assertEquals(0, inDiningRoom);
                }
                case GREEN, PINK -> {
                    assertEquals(1, inEntrance);
                    assertEquals(0, inDiningRoom);
                }
                case BLUE, YELLOW -> {
                    assertEquals(0, inEntrance);
                    assertEquals(1, inDiningRoom);
                }
            }
        }
    }

    @Test
    void applyEffect_ThreePairsOfStudentsToSwap_InvalidArgumentException() {
        List<Color> src = List.of(Color.BLUE, Color.BLUE, Color.YELLOW);
        List<Color> dst = List.of(Color.RED, Color.GREEN, Color.PINK);
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
    }

    @Test
    void applyEffect_NullSourceOrDestination_InvalidArgumentException() {
        List<Color> src = List.of(Color.BLUE);
        List<Color> dst = List.of(Color.RED);
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, null, null, null));
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(null, dst, null, null));
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, null, null, null));
    }

    @Test
    void applyEffect_DifferentSizeSourceAndDestination_InvalidArgumentException() {
        List<Color> src = List.of(Color.YELLOW);
        List<Color> dst = List.of(Color.GREEN, Color.PINK);
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
    }

    @Test
    void applyEffect_EmptySourceAndDestination_InvalidArgumentException() {
        List<Color> src = List.of();
        List<Color> dst = List.of();
        assertThrows(InvalidArgumentException.class, () -> card.applyEffect(src, dst, null, null));
    }

    @Test
    void applyEffect_StudentNotOnCard_NoMovementException() {
        StudentContainer entrance = player.getEntrance();
        StudentContainer diningRoom = player.getDiningRoom();
        Bag bag = new Bag();
        List<Color> src = List.of(Color.BLUE);
        List<Color> dst = List.of(Color.RED);
        assertDoesNotThrow(() -> {
            bag.moveTo(entrance, Color.YELLOW);
            bag.moveTo(entrance, Color.RED);
            bag.moveTo(diningRoom, Color.BLUE);
        });
        assertThrows(NoMovementException.class, () -> card.applyEffect(src, dst, null, null));
    }
}