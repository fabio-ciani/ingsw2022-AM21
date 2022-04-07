package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "jester" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Jester extends ContainerCharacterCard {
    /**
     * Supplier to get the current player (the one who activated the effect of this card).
     *
     * @see Supplier
     */
    private final Supplier<Player> currentPlayerSupplier;

    /**
     * Amount of students that can be placed on top of a {@link Jester} card.
     */
    private static final int SIZE = 6;

    /**
     * Initial cost to activate the {@link Jester} effect.
     */
    private static final int INITIAL_COST = 1;

    /**
     * Maximum number of students that can be selected on this card to swap.
     */
    private static final int MAX_MOVEMENTS = 3;

    /**
     * Constructs a new {@link Jester} character card.
     *
     * @param bag Reference to the bag object for the current game.
     * @param currentPlayerSupplier Supplier to get the current player.
     */
    public Jester(Bag bag, Supplier<Player> currentPlayerSupplier) {
        super(SIZE, INITIAL_COST, bag);
        this.currentPlayerSupplier = currentPlayerSupplier;
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, InvalidArgumentException {
        if (sourceColors.size() != destinationColors.size() || sourceColors.size() > MAX_MOVEMENTS) {
            throw new InvalidArgumentException(String.format("Invalid amount of students to swap (%d from source and %d from destination).", sourceColors.size(), destinationColors.size()));
        }
        StudentContainer entrance = currentPlayerSupplier.get().getEntrance();
        for (int i = 0; i < sourceColors.size(); i++) {
            swap(entrance, sourceColors.get(i), destinationColors.get(i));
        }
        increaseCost();
    }
}
