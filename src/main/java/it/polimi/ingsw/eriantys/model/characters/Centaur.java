package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import it.polimi.ingsw.eriantys.model.influence.NoTowersInfluence;

import java.util.List;

/**
 * Represents the "centaur" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Centaur extends InfluenceCharacterCard {
    /**
     * Initial cost to activate the {@link Centaur} effect.
     */
    private static final int INITIAL_COST = 3;

    /**
     * Constructs a new {@link Centaur} character card.
     *
     * @param gameManager Reference to the {@link GameManager} for the current game.
     */
    public Centaur(GameManager gameManager) {
        super(INITIAL_COST, gameManager);
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland) throws IllegalInfluenceStateException {
        effectInfluenceCalculator = new NoTowersInfluence();
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
    }
}
