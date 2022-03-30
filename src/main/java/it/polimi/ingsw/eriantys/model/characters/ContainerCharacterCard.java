package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;

/**
 * Abstract class that represents the character cards that require to contain students to use when activating the effect.
 */
public abstract class ContainerCharacterCard extends StudentContainer implements CharacterCard {
    /**
     * Initial cost to activate the effect.
     */
    private final int initialCost;

    /**
     * Whether the cost is incremented by 1 ({@code true} after first activation).
     */
    private boolean increaseCost = false;

    /**
     * Reference to the {@link Bag} from which to draw the students.
     */
    protected final Bag bag;

    /**
     * Constructs the character card.
     *
     * @param maxSize Maximum number of students on this card.
     * @param initialCost Initial cost to activate the effect.
     * @param bag Reference to the {@link Bag} object for the current game.
     */
    public ContainerCharacterCard(int maxSize, int initialCost, Bag bag) {
        super(maxSize);
        this.initialCost = initialCost;
        this.bag = bag;
    }

    @Override
    public int getCost() {
        return increaseCost ? initialCost + 1 : initialCost;
    }

    @Override
    public void setupEffect() throws NoMovementException {
        refillFrom(bag);
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException {
        if (!increaseCost) {
            increaseCost = true;
        }
    }

    @Override
    public void cancelEffect() {}
}
