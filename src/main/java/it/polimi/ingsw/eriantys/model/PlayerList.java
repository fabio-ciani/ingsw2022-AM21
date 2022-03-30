package it.polimi.ingsw.eriantys.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A {@link GameManager} helper class to handle the players of the game,
 * in particular for the variability of the turn order.
 */
public class PlayerList {
    private final Player[] players;
    private int firstInRound;

    public PlayerList(List<String> nicknames) {
        players = new Player[nicknames.size()];
        for (int i = 0; i < nicknames.size(); i++)
            players[i] = new Player(nicknames.get(i));
    }

    // TODO: Do we need to pass to setFirst() method a Player or a String? Should we use rotate()?

    // TODO: getTurnOrder() and its test

    /**
     * A getter for the {@link Player} object associated to a nickname.
     * @param nickname the identificator of the {@link Player}
     * @return the {@link Player} corresponding to the given nickname if it exists, {@code null} otherwise
     */
    public Player get(String nickname) {
        Player temp = new Player(nickname);
        int index = new ArrayList<>(Arrays.asList(players)).indexOf(temp);

        if (index != -1)
            return players[index];
        return null;
    }
}