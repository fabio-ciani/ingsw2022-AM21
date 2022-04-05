package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.IllegalInfluenceStateException;
import it.polimi.ingsw.eriantys.model.exceptions.ItemNotAvailableException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;

/**
 * The {@code CharacterCard} class is used to handle character effects in expert mode games.
 */
public interface CharacterCard {
    /**
     * @return The amount of coins needed to activate the effect of this card.
     */
    int getCost();

    // TODO: 26/03/2022 getRoutine() - Is this the right way?

    /**
     * Prepares this card to be activated.
     * It should be called once at the beginning of the game.
     *
     * @throws NoMovementException If students couldn't be moved correctly.
     */
    void setupEffect() throws NoMovementException;

    /**
     * Activates this card and applies the effect.
     * If it's the first time also increases the cost for next activations.
     * Only some of the arguments might be used for a specific character card effect.
     *
     * @param sourceColors List of student colors selected from the source.
     * @param destinationColors List of student colors selected from the destination.
     * @param targetColor Color selected for the effect.
     * @param targetIsland Island selected for the effect.
     */
    void applyEffect(List<Color> sourceColors,
                     List<Color> destinationColors,
                     Color targetColor,
                     IslandGroup targetIsland)
            throws NoMovementException, ItemNotAvailableException, IllegalInfluenceStateException;

    /**
     * Resets the effects of this card that are not intended to persist after the activation turn.
     * It should be called at the end of the turn.
     */
    void cancelEffect() throws IllegalInfluenceStateException;
}
