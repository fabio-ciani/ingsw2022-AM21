package it.polimi.ingsw.eriantys.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerListTest {
    Player Alice = new Player("Alice");
    Player Bob = new Player("Bob");
    Player Eve = new Player("Eve");

    List<String> players = new ArrayList<>();
    PlayerList l;

    @BeforeEach
    void init() {
        players.add(Alice.getNickname());
        players.add(Bob.getNickname());
        players.add(Eve.getNickname());

        l = new PlayerList(players);
    }

    @Test
    void setFirst_NicknameNotFound_NoChange() {
        l.setFirst(new Player("admin"));

        List<Player> turnOrder = l.getTurnOrder();

        assertEquals(turnOrder.get(0), Alice);
        assertEquals(turnOrder.get(1), Bob);
        assertEquals(turnOrder.get(2), Eve);
    }

    @Test
    void SetFirstAndGetTurnOrder() {
        l.setFirst(Eve);

        List<Player> turnOrder = l.getTurnOrder();

        assertEquals(turnOrder.get(0), Eve);
        assertEquals(turnOrder.get(1), Alice);
        assertEquals(turnOrder.get(2), Bob);
    }

    @Test
    void get_NicknameFound_NormalPostConditions() {
        Player ans = l.get(new String("Eve"));

        assertEquals(ans, Eve);
    }

    @Test
    void get_NicknameNotFound_ReturnNull() {
        Player ans = l.get(new String("admin"));

        assertNull(ans);
    }
}