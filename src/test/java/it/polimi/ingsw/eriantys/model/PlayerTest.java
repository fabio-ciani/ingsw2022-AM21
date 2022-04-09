package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void setTowerColor() {
        Player p = new Player("admin");

        p.setTowerColor(TowerColor.BLACK);
        assertSame(p.getTowerColor(), TowerColor.BLACK);
    }

    @Test
    void setWizard() {
        Player p = new Player("admin");

        p.setWizard(Wizard.DESERT_WIZARD);
        assertSame(p.getWizard(), Wizard.DESERT_WIZARD);
    }

    @Test
    void getEntrance() {
        Player p = new Player("admin");

        assertNotNull(p.getEntrance());
    }

    @Test
    void getDiningRoom() {
        Player p = new Player("admin");

        assertNotNull(p.getDiningRoom());
    }

    @Test
    void setMotherNatureMovements_PositiveParameter_NormalPostConditions() {
        Player p = new Player("admin");

        assertDoesNotThrow(() -> p.setMotherNatureMovements(3));
        assertSame(p.getMotherNatureMovements(), 3);
    }

    @Test
    void setMotherNatureMovements_NegativeParameter_ThrowException() {
        Player p = new Player("admin");

        assertThrowsExactly(InvalidArgumentException.class, () -> p.setMotherNatureMovements(-10));
    }

    @Test
    void updateCoins() {
        Player p = new Player("admin");

        p.updateCoins(7);
        assertSame(p.getCoins(), 1 + 7);

        p.updateCoins(-5);
        assertSame(p.getCoins(), 3);
    }

    @Test
    void equals() {
        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");

        assertNotEquals(p1, p2);

        Player p3 = new Player("Alice");

        assertEquals(p1, p3);
    }
}