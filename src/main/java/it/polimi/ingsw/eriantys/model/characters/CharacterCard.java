package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.*;

import java.util.List;

/**
 * The {@code CharacterCard} class is used to handle character effects in expert mode games.
 */
public interface CharacterCard {
	/**
	 * A getter for the amount of coins needed to activate the effect of this card.
	 *
	 * @return the cost of the card
	 */
	int getCost();

	/**
	 * Prepares this card to be activated.
	 * It should be called once at the beginning of the game.
	 *
	 * @throws InvalidArgumentException if (eventual) parameters are illegal
	 * @throws NoMovementException if students couldn't be moved correctly
	 */
	void setupEffect() throws InvalidArgumentException, NoMovementException;

	/**
	 * Activates this card and applies the effect.
	 * If it is the first time also increases the cost for next activations.
	 * Only some arguments might be used for a specific character card effect.
	 *
	 * @param sourceColors a list of student colors selected from the source
	 * @param destinationColors a list of student colors selected from the destination
	 * @param targetColor the color selected for the effect
	 * @param targetIsland the island selected for the effect
	 *
	 * @throws NoMovementException if an error occurs while wielding student related movements and swaps
	 * @throws ItemNotAvailableException if an error occurs while wielding a game item
	 * @throws DuplicateNoEntryTileException if an error occurs while wielding no-entry tiles
	 * @throws InvalidArgumentException if one or more parameters are illegal
	 */
	void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws DuplicateNoEntryTileException, InvalidArgumentException, ItemNotAvailableException, NoMovementException;

	/**
	 * Resets the effects of this card that are not intended to persist after the activation turn.
	 * It should be called at the end of the turn.
	 *
	 * @throws InvalidArgumentException if one or more (eventual) parameters needed for the call are illegal
	 */
	void cancelEffect() throws InvalidArgumentException;

	/**
	 * A getter for the name of the class, representing the character name.
	 * @return the simple name of the card
	 */
	default String getName() {
		return this.getClass().getSimpleName();
	}
}
