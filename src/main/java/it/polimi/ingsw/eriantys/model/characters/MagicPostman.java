package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "magic postman" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class MagicPostman extends BaseCharacterCard {
	/**
	 * Supplier to get the current player (the one who activated the effect of this card).
	 *
	 * @see Supplier
	 */
	private final Supplier<Player> currentPlayerSupplier;

	/**
	 * Initial cost to activate the {@link MagicPostman} effect.
	 */
	private static final int INITIAL_COST = 1;

	private static final int BONUS_MOVEMENTS = 2;

	/**
	 * Constructs a new {@link MagicPostman} character card.
	 *
	 * @param currentPlayerSupplier Supplier to get the current player.
	 */
	public MagicPostman(Supplier<Player> currentPlayerSupplier) {
		super(INITIAL_COST);
		this.currentPlayerSupplier = currentPlayerSupplier;
	}

	@Override
	public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws InvalidArgumentException {
		Player currentPlayer = currentPlayerSupplier.get();
		int MNMovements = currentPlayer.getMotherNatureMovements();
		currentPlayer.setMotherNatureMovements(MNMovements + BONUS_MOVEMENTS);
		increaseCost();
	}
}
