package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Bag;
import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.exceptions.NoMovementException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "spoiled princess" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class SpoiledPrincess extends ContainerCharacterCard {
	/**
	 * Supplier to get the current player (the one who activated the effect of this card).
	 *
	 * @see Supplier
	 */
	private final Supplier<Player> currentPlayerSupplier;

	/**
	 * Amount of students that can be placed on top of a {@link SpoiledPrincess} card.
	 */
	private static final int SIZE = 4;

	/**
	 * Initial cost to activate the {@link SpoiledPrincess} effect.
	 */
	private static final int INITIAL_COST = 2;

	/**
	 * Constructs a new {@link SpoiledPrincess} character card.
	 *
	 * @param bag Reference to the bag object for the current game.
	 * @param currentPlayerSupplier Supplier to get the current player.
	 */
	public SpoiledPrincess(Bag bag, Supplier<Player> currentPlayerSupplier) {
		super(SIZE, INITIAL_COST, bag);
		this.currentPlayerSupplier = currentPlayerSupplier;
	}

	@Override
	public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws InvalidArgumentException, NoMovementException {
		if (targetColor == null) {
			throw new InvalidArgumentException("No student was selected");
		}
		StudentContainer diningRoom = currentPlayerSupplier.get().getDiningRoom();
		moveTo(diningRoom, targetColor);
		bag.moveTo(this, 1);
		increaseCost();
	}
}
