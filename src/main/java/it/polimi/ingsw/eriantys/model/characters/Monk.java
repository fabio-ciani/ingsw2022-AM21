package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;

/**
 * Represents the "monk" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Monk extends ContainerCharacterCard {
	/**
	 * Amount of students that can be placed on top of a {@link Monk} card.
	 */
	private static final int SIZE = 4;

	/**
	 * Initial cost to activate the {@link Monk} effect.
	 */
	private static final int INITIAL_COST = 1;

	/**
	 * Constructs a new {@link Monk} character card.
	 *
	 * @param bag Reference to the bag object for the current game.
	 */
	public Monk(Bag bag) {
		super(SIZE, INITIAL_COST, bag);
	}

	@Override
	public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws NoMovementException, InvalidArgumentException {
		if (targetColor == null) {
			throw new InvalidArgumentException("targetColor argument is null.");
		}
		if (targetIsland == null) {
			throw new InvalidArgumentException("targetIsland argument is null.");
		}
		moveTo(targetIsland, targetColor);
		bag.moveTo(this, 1);
		increaseCost();
	}
}
