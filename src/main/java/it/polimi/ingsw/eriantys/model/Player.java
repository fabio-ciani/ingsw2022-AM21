package it.polimi.ingsw.eriantys.model;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

/**
 * A class which represents a human being playing the game.
 * Its internal state contains all the game elements that are used and manipulated by the player.
 */
public class Player {
    private final String nickname;
    private final SchoolBoard schoolBoard;
    private TowerColor teamColor;
    private Wizard wizard;
    private final List<AssistantCard> deck;
    private int motherNatureMovements, coins;

    public Player(String nickname) {
        this.nickname = nickname;
        schoolBoard = new SchoolBoard();
        teamColor = null;
        wizard = null;

        deck = new ArrayList<>();
        for (AssistantCard c : AssistantCard.values())
            deck.add(c);

        motherNatureMovements = 0;
        coins = 1;
    }

    /**
     * A getter for the {@link TowerColor} of a {@code Player}'s object.
     * @return The internal state for a {@code Player}'s {@link TowerColor}.
     */
    public TowerColor getTowerColor() {
        return teamColor;
    }

    /**
     * A setter for the {@link TowerColor} of a {@code Player}'s object.
     * @param teamColor The new internal state for a {@code Player}'s {@link TowerColor}.
     */
    public void setTowerColor(TowerColor teamColor) {
        this.teamColor = teamColor;
    }

    /**
     * A getter for the {@link Wizard} of a {@code Player}'s object.
     * @return The internal state for a {@code Player}'s {@link Wizard}.
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * A setter for the {@link Wizard} of a {@code Player}'s object.
     * @param wizard The new internal state for a {@code Player}'s {@link Wizard}.
     */
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    /**
     * A getter for the {@link SchoolBoard} of a {@code Player}'s object.
     * @return The reference of a {@code Player}'s {@link SchoolBoard}.
     */
    public SchoolBoard getSchoolBoard() {
        return schoolBoard;
    }

    /**
     * A getter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
     * @return The internal state for the number of allowed Mother Nature's movements of a {@code Player}.
     */
    public int getMotherNatureMovements() {
        return motherNatureMovements;
    }

    /**
     * A setter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
     * @param movements The new internal state for the number of allowed Mother Nature's movements of a {@code Player}.
     */
    public void setMotherNatureMovements(int movements) {
        motherNatureMovements = movements;
    }

    /**
     * A getter for the number of coins for a {@code Player}'s object.
     * @return The internal state for the number of coins of a {@code Player}.
     */
    public int getCoins() {
        return coins;
    }

    public void drawStudents() {
        // TODO: The implementation of GameManager and its handleSelectedCloud(...) method is required
    }

    /**
     * A method to assign a variable quantity of coins to a {@code Player}.
     * @param amount The number of coins (could be negative) to be assigned to the {@code Player}.
     */
    public void updateCoins(int amount) {
        coins += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(nickname, player.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}