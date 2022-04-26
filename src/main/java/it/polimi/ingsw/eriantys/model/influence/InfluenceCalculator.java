package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.Set;

/**
 * The influence calculation during the game is operated by a calculator entity.
 * This interface encloses a state pattern, which will be implemented by concrete classes.
 */
public interface InfluenceCalculator {
	/**
	 * The method evaluates the influence of a {@link Player} on a certain island.
	 * @param player the main target of the calculation
	 * @param island the game location used for the calculation
	 * @param ownedProfessors the {@link Color}(s) of the professors which the {@link Player} owns
	 * @return the influence value for the given {@link Player} on the specified island
	 */
	int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors) throws InvalidArgumentException;
}