package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import it.polimi.ingsw.eriantys.model.influence.CommonInfluence;
import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

import java.util.List;

/**
 * Abstract class that represents character cards which effects alter influence calculations.
 */
public abstract class InfluenceCharacterCard extends BaseCharacterCard {
    /**
     * Base {@link InfluenceCalculator} to set when the effect is canceled.
     */
    private final InfluenceCalculator normalInfluenceCalculator;

    /**
     * {@link InfluenceCalculator} to set when the effect is activated.
     */
    protected InfluenceCalculator effectInfluenceCalculator;

    /**
     * Reference to the {@link GameManager} object for the current game.
     */
    protected final GameManager gameManager;

    /**
     * Constructs the character card.
     *
     * @param initialCost Initial cost to activate the effect.
     * @param gameManager Reference to the {@link GameManager} object for the current game.
     */
    public InfluenceCharacterCard(int initialCost, GameManager gameManager) {
        super(initialCost);
        this.gameManager = gameManager;
        this.normalInfluenceCalculator = new CommonInfluence();
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland) throws IllegalInfluenceStateException {
        gameManager.changeInfluenceState(effectInfluenceCalculator);
        increaseCost();
    }

    @Override
    public void cancelEffect() throws IllegalInfluenceStateException {
        super.cancelEffect();
        gameManager.changeInfluenceState(normalInfluenceCalculator);
    }
}
