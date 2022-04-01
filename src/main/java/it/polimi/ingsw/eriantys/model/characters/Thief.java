package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.Collection;
import java.util.List;

/**
 * Represents the "thief" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Thief extends BaseCharacterCard {
    /**
     * Collection of all players in the current game.
     */
    private final Collection<Player> players;

    /**
     * Reference to the {@link Bag} object for the current game.
     */
    private final Bag bag;

    /**
     * Initial cost to activate the {@link Thief} effect.
     */
    private static final int INITIAL_COST = 3;

    /**
     * Number of students (of the chosen {@link Color}) to remove from all dining rooms.
     * If a player has less than {@code STUDENTS_TO_REMOVE} students of that {@link Color}, they are all removed.
     *
     * @see SchoolBoard
     */
    private static final int STUDENTS_TO_REMOVE = 3;

    /**
     * Constructs a new {@link Thief} character card.
     *
     * @param players Collection of all players in the current game.
     * @param bag Reference to the {@link Bag} object for the current game.
     */
    public Thief(Collection<Player> players, Bag bag) {
        super(INITIAL_COST);
        this.players = players;
        this.bag = bag;
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException {
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
        for (Player player : players) {
            boolean emptyColor = false;
            for (int i = 0; i < STUDENTS_TO_REMOVE && !emptyColor; i++) {
                try {
                    player.getDiningRoom().moveTo(bag, targetColor);
                } catch (NoMovementException exception) {
                    emptyColor = true;
                }
            }
        }
    }
}
