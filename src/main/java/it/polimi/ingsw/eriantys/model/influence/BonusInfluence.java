package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;
import it.polimi.ingsw.eriantys.model.characters.Knight;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.Set;

/**
 * This concrete implementation for the state design pattern involving {@link InfluenceCalculator}
 * defines the {@link Knight}'s {@link CharacterCard} effect.
 */
public class BonusInfluence extends CommonInfluence implements InfluenceCalculator {
	private final Player user;

	public BonusInfluence(Player user) {
		this.user = user;
	}

	@Override
	public int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors) throws InvalidArgumentException {
		if (user == null)
			throw new InvalidArgumentException("Cannot proceed with null internal attributes parameter(s).");

		int result = super.calculate(player, island, ownedProfessors);

		return (user.equals(player)) ? result + 2 : result;
	}
}