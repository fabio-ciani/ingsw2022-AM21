package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.*;

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
	void setupEffect() throws InvalidArgumentException, NoMovementException;

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
	void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws NoMovementException, ItemNotAvailableException, DuplicateNoEntryTileException, InvalidArgumentException;

	/**
	 * Resets the effects of this card that are not intended to persist after the activation turn.
	 * It should be called at the end of the turn.
	 */
	void cancelEffect() throws InvalidArgumentException;

	// TODO: Is this allowed? + How to split PascalCase into separate words? (needed to handle characters.json correctly)
	// SOLUTION: We could maintain PascalCase and let the user insert the card name with spaces. Then, we could use string replace in order to match the JSON values.
	/**
	 * A getter for the name of the class, representing the character name.
	 * @return the simple name of the card
	 */
	default String getName() {
		return this.getClass().getSimpleName();
	}
}
