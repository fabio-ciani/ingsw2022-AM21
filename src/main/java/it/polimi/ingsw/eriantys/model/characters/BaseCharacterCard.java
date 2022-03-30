package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;

/**
 * Abstract class that represents character cards that don't hold students.
 */
public abstract class BaseCharacterCard implements CharacterCard {
    /**
     * Initial cost to activate the effect.
     */
    private final int initialCost;

    /**
     * Whether the cost is incremented by 1 ({@code true} after first activation).
     */
    private boolean increaseCost = false;

    /**
     * Constructs the character card.
     *
     * @param initialCost Initial cost to activate the effect.
     */
    public BaseCharacterCard(int initialCost) {
        this.initialCost = initialCost;
    }

    @Override
    public int getCost() {
        return increaseCost ? initialCost + 1 : initialCost;
    }

    @Override
    public void setupEffect() throws NoMovementException {}

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException {
        if (!increaseCost) {
            increaseCost = true;
        }
    }

    @Override
    public void cancelEffect() {}
}
