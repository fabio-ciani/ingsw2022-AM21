package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

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
    private boolean increasedCost = false;

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
        return increasedCost ? initialCost + 1 : initialCost;
    }

    protected void increaseCost() {
        if (!increasedCost) {
            increasedCost = true;
        }
    }

    @Override
    public void setupEffect() throws InvalidArgumentException {}

    @Override
    public void cancelEffect() throws InvalidArgumentException {}
}
