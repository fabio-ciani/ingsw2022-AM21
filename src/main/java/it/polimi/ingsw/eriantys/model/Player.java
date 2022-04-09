package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class which represents a human being playing the game.
 * Its internal state contains all the game elements that are used and manipulated by the player.
 */
public class Player {
    private final String nickname;
    private final SchoolBoard schoolBoard;
    private TowerColor teamColor;
    private Wizard wizard;
    private final List<AssistantCard> deck; // TODO: 09/04/2022 Check setter?
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
     * A getter for the nickname of a {@code Player}'s object.
     * @return the internal state for a {@code Player}'s nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * A getter for the {@link TowerColor} of a {@code Player}'s object.
     * @return the internal state for a {@code Player}'s {@link TowerColor}
     */
    public TowerColor getTowerColor() {
        return teamColor;
    }

    /**
     * A setter for the {@link TowerColor} of a {@code Player}'s object.
     * @param teamColor the new internal state for a {@code Player}'s {@link TowerColor}
     */
    public void setTowerColor(TowerColor teamColor) {
        this.teamColor = teamColor;
    }

    /**
     * A getter for the {@link Wizard} of a {@code Player}'s object.
     * @return the internal state for a {@code Player}'s {@link Wizard}
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * A setter for the {@link Wizard} of a {@code Player}'s object.
     * @param wizard the new internal state for a {@code Player}'s {@link Wizard}
     */
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    /**
     * A getter for the {@link SchoolBoard} entrance of a {@code Player}'s object.
     * @return the reference to the {@code Player}'s {@link SchoolBoard} entrance
     */
    public StudentContainer getEntrance() {
        return schoolBoard.getEntrance();
    }

    /**
     * A getter for the {@link SchoolBoard} dining room of a {@code Player}'s object.
     * @return the reference to the {@code Player}'s {@link SchoolBoard} dining room
     */
    public DiningRoom getDiningRoom() {
        return schoolBoard.getDiningRoom();
    }

    /**
     * A getter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
     * @return the internal state for the number of allowed Mother Nature's movements of a {@code Player}
     */
    public int getMotherNatureMovements() {
        return motherNatureMovements;
    }

    /**
     * A setter for the number of allowed Mother Nature's movements for a {@code Player}'s object during the current turn.
     * @param movements the new internal state for the number of allowed Mother Nature's movements of a {@code Player}
     */
    public void setMotherNatureMovements(int movements) throws InvalidArgumentException {
        if (movements < 0)
            throw new InvalidArgumentException("Parameter should not be negative.");
        motherNatureMovements = movements;
    }

    /**
     * A getter for the number of coins for a {@code Player}'s object.
     * @return the internal state for the number of coins of a {@code Player}
     */
    public int getCoins() {
        return coins;
    }

    /**
     * A method to assign a variable quantity of coins to a {@code Player}.
     * @param amount the number of coins (could be negative) to be assigned to the {@code Player}
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