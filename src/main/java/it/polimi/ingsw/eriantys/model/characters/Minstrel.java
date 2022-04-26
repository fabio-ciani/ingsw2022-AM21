package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "minstrel" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Minstrel extends BaseCharacterCard {
	/**
	 * Supplier to get the current player (the one who activated the effect of this card).
	 *
	 * @see Supplier
	 */
	private final Supplier<Player> currentPlayerSupplier;

	/**
	 * Initial cost to activate the {@link Minstrel} effect.
	 */
	private static final int INITIAL_COST = 1;

	/**
	 * Maximum number of students that can be selected from the entrance and the dining room to swap ({@code MAX_MOVEMENTS} students each).
	 */
	private static final int MAX_SWAPS = 2;

	/**
	 * Constructs a new {@link Minstrel} character card.
	 */
	public Minstrel(Supplier<Player> currentPlayerSupplier) {
		super(INITIAL_COST);
		this.currentPlayerSupplier = currentPlayerSupplier;
	}

	@Override
	public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws NoMovementException, InvalidArgumentException {
		if (sourceColors == null) {
			throw new InvalidArgumentException("sourceColors argument is null.");
		}
		if (destinationColors == null) {
			throw new InvalidArgumentException("destinationColors argument is null.");
		}
		if (sourceColors.size() != destinationColors.size()) {
			throw new InvalidArgumentException(String.format("Invalid amount of students to swap: %d from source and %d from destination (should be the same number).", sourceColors.size(), destinationColors.size()));
		}
		if (sourceColors.size() > MAX_SWAPS) {
			throw new InvalidArgumentException(String.format("Invalid amount of students to swap: more than %d students selected.", MAX_SWAPS));
		}
		if (sourceColors.size() == 0) {
			throw new InvalidArgumentException("Invalid amount of students to swap: no students selected.");
		}
		StudentContainer entrance = currentPlayerSupplier.get().getEntrance();
		StudentContainer diningRoom = currentPlayerSupplier.get().getDiningRoom();
		for (int i = 0; i < sourceColors.size(); i++) {
			entrance.swap(diningRoom, sourceColors.get(i), destinationColors.get(i));
		}
		increaseCost();
	}
}
