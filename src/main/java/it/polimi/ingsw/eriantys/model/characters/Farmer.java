package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;

import java.util.List;

/**
 * Represents the "farmer" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Farmer extends BaseCharacterCard {
    /**
     * Reference to the {@link ProfessorOwnership} object for the current game.
     */
    private final ProfessorOwnership professorOwnership;

    /**
     * Initial cost to activate the {@link Farmer} effect.
     */
    private static final int INITIAL_COST = 2;

    /**
     * Constructs a new {@link Farmer} character card.
     *
     * @param professorOwnership Reference to the {@link ProfessorOwnership} object for the current game.
     */
    public Farmer(ProfessorOwnership professorOwnership) {
        super(INITIAL_COST);
        this.professorOwnership = professorOwnership;
    }

    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland) {
        professorOwnership.activateEffect();
        increaseCost();
    }

    /**
     *
     */
    @Override
    public void cancelEffect() {
        professorOwnership.deactivateEffect();
    }
}
