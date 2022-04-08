package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
    Player Alice = new Player("Alice");
    Player Bob = new Player("Bob");
    Player Eve = new Player("Eve");

    List<String> players = new ArrayList<>();

    @BeforeEach
    void init() {
        players.add(Alice.getNickname());
        players.add(Bob.getNickname());
        players.add(Eve.getNickname());
    }

    @Test
    void HandleAssistantCardsAndGetTurnOrder() {
        GameManager gm = new GameManager(players, false);

        Map<Player, AssistantCard> playedCards = new HashMap<>();

        playedCards.put(Alice, AssistantCard.FOX);
        playedCards.put(Bob, AssistantCard.TURTLE);
        playedCards.put(Eve, AssistantCard.CHEETAH);

        gm.handleAssistantCards(playedCards);

        List<String> turnOrder = gm.getTurnOrder();

        assertEquals(turnOrder.get(0), Eve.getNickname());
        assertEquals(turnOrder.get(1), Alice.getNickname());
        assertEquals(turnOrder.get(2), Bob.getNickname());
    }

    @Test
    void changeInfluenceState_PassNull_ThrowException() {
        GameManager gm = new GameManager(players, false);

        assertThrowsExactly(InvalidArgumentException.class, () -> gm.changeInfluenceState(null));
    }
}