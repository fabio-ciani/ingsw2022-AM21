package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "minstrel" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Minstrel extends BaseCharacterCard {
    /**
     * Supplier to get the current player (the one who activated the effect of this card).
     *
     * @see Supplier
     */
    private final Supplier<Player> currentPlayerSupplier;

    /**
     * Initial cost to activate the {@link Minstrel} effect.
     */
    private static final int INITIAL_COST = 1;

    /**
     * Maximum number of students that can be selected from the entrance and the dining room to swap ({@code MAX_MOVEMENTS} students each).
     */
    private static final int MAX_MOVEMENTS = 2;

    /**
     * Constructs a new {@link Minstrel} character card.
     */
    public Minstrel(Supplier<Player> currentPlayerSupplier) {
        super(INITIAL_COST);
        this.currentPlayerSupplier = currentPlayerSupplier;
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException {
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
        if (sourceColors.size() != destinationColors.size() || sourceColors.size() > MAX_MOVEMENTS) {
            throw new IllegalArgumentException();
        }
        StudentContainer entrance = currentPlayerSupplier.get().getEntrance();
        StudentContainer diningRoom = currentPlayerSupplier.get().getDiningRoom();
        for (int i = 0; i < sourceColors.size(); i++) {
            entrance.swap(diningRoom, sourceColors.get(i), destinationColors.get(i));
        }
    }
}
