package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.function.Supplier;

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
     * Supplier to get the current player (the one who activated the effect of this card).
     *
     * @see Supplier
     */
    private final Supplier<Player> currentPlayerSupplier;

    /**
     * Initial cost to activate the {@link Farmer} effect.
     */
    private static final int INITIAL_COST = 2;

    /**
     * Constructs a new {@link Farmer} character card.
     *
     * @param professorOwnership Reference to the {@link ProfessorOwnership} object for the current game.
     * @param currentPlayerSupplier Supplier to get the current player.
     */
    public Farmer(ProfessorOwnership professorOwnership, Supplier<Player> currentPlayerSupplier) {
        super(INITIAL_COST);
        this.professorOwnership = professorOwnership;
        this.currentPlayerSupplier = currentPlayerSupplier;
    }

    /**
     *
     */
    @Override
    public void applyEffect(List<Color> sourceColors,
                            List<Color> destinationColors,
                            Color targetColor,
                            IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException, IllegalInfluenceStateException {
        super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
        // TODO: 30/03/2022 Change professorOwnership state
        Player currentPlayer = currentPlayerSupplier.get();
        // ProfessorUpdater updater = new StealOnTieUpdater(currentPlayer);
        // professorOwnership.setUpdater(updater);
    }

    /**
     *
     */
    @Override
    public void cancelEffect() throws IllegalInfluenceStateException {
        super.cancelEffect();
        // TODO: 30/03/2022 Change back professorOwnership state
        // ProfessorUpdater updater = new NoEffectOnTieUpdater();
        // professorOwnership.setUpdater(updater);
    }
}
