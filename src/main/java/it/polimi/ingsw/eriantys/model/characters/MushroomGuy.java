package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.GameManager;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;
import it.polimi.ingsw.eriantys.model.influence.ExcludeColorInfluence;

import java.util.List;

/**
 * Represents the "mushroom guy" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class MushroomGuy extends InfluenceCharacterCard {
    /**
     * Initial cost to activate the {@link MushroomGuy} effect.
     */
    private static final int INITIAL_COST = 3;

    /**
     * Constructs a new {@link MushroomGuy} character card.
     *
     * @param gameManager Reference to the {@link GameManager} for the current game.
     */
    public MushroomGuy(GameManager gameManager) {
        super(INITIAL_COST, gameManager);
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException {
        effectInfluenceCalculator = new ExcludeColorInfluence(targetColor);
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
    }
}
