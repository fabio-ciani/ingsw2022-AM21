package it.polimi.ingsw.eriantys.model.influence;

import it.polimi.ingsw.eriantys.model.Color;
import it.polimi.ingsw.eriantys.model.IslandGroup;
import it.polimi.ingsw.eriantys.model.Player;
import it.polimi.ingsw.eriantys.model.characters.Centaur;
import it.polimi.ingsw.eriantys.model.characters.CharacterCard;
import it.polimi.ingsw.eriantys.model.exceptions.InvalidArgumentException;

import java.util.Set;

/**
 * This concrete implementation for the state design pattern involving {@link InfluenceCalculator}
 * defines the {@link Centaur}'s {@link CharacterCard} effect.
 */
public class NoTowersInfluence extends CommonInfluence implements InfluenceCalculator {
    public int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors) throws InvalidArgumentException {
        if (player == null || island == null || ownedProfessors == null)
            throw new InvalidArgumentException("Cannot proceed with null parameter(s).");

        int result = 0;

        for (Color c : ownedProfessors)
            result += evaluateColor(island, c);

        return result;
    }
}