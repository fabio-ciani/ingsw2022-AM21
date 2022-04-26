package it.polimi.ingsw.eriantys.model.characters;

import it.polimi.ingsw.eriantys.model.*;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;
import it.polimi.ingsw.eriantys.model.influence.BonusInfluence;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the "knight" character card and the corresponding effect.
 *
 * @see CharacterCard
 */
public class Knight extends InfluenceCharacterCard {
	/**
	 * Supplier to get the current player (the one who activated the effect of this card).
	 *
	 * @see Supplier
	 */
	private final Supplier<Player> currentPlayerSupplier;

	/**
	 * Initial cost to activate the {@link Knight} effect.
	 */
	private static final int INITIAL_COST = 2;

	/**
	 * Constructs a new {@link Knight} character card.
	 *
	 * @param gameManager Reference to the {@link GameManager} for the current game.
	 * @param currentPlayerSupplier Supplier to get the current player.
	 */
	public Knight(GameManager gameManager, Supplier<Player> currentPlayerSupplier) {
		super(INITIAL_COST, gameManager);
		this.currentPlayerSupplier = currentPlayerSupplier;
	}

	@Override
	public void applyEffect(List<Color> sourceColors, List<Color> destinationColors, Color targetColor, IslandGroup targetIsland)
			throws InvalidArgumentException {
		Player currentPlayer = currentPlayerSupplier.get();
		effectInfluenceCalculator = new BonusInfluence(currentPlayer);
		super.applyEffect(sourceColors, destinationColors, targetColor, targetIsland);
	}
}
