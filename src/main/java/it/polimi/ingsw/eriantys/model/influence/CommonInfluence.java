package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.StudentContainer;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.Set;

/**
 * This concrete implementation for the state design pattern involving {@link InfluenceCalculator}
 * defines the influence calculation when it is not affected by any {@link CharacterCard}.
 */
public class CommonInfluence implements InfluenceCalculator {
    @Override
    public int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors) throws InvalidArgumentException {
        if (player == null || island == null || ownedProfessors == null)
            throw new InvalidArgumentException("Cannot proceed with null parameter(s).");

        int result = 0;

        for (Color c : ownedProfessors)
            result += evaluateColor(island, c);     // TODO: Should we suppress the for loop by putting it inside evaluateColor(...)? (ExcludeColorInfluence will need to do an @Override)
        result += evaluateTowers(island, player);

        return result;
    }

    /**
     * The method evaluates the quantity of students of a {@link Color} on an island.
     * @param island the game location used for the calculation
     * @param c the desired target {@link Color}
     * @return the number of students of the given {@link Color} on the specified island
     */
    protected int evaluateColor(StudentContainer island, Color c) {
        return island.getQuantity(c);
    }

    /**
     * The method evaluates the contribution of the towers on an island.
     * @param island the game location used for the calculation
     * @param player the target {@link Player}
     * @return the contribution of the towers on the specified island if the passed {@link Player} parameter is equal to the controller of the island
     */
    protected int evaluateTowers(IslandGroup island, Player player) {
        return (island.getController().equals(player)) ? island.getTowers() : 0;
    }
}