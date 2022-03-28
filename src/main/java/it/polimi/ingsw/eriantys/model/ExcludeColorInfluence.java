package it.polimi.ingsw.eriantys.model;

import java.util.Set;

/**
 * This concrete implementation for the state design pattern involving {@link InfluenceCalculator}
 * defines the {@link it.polimi.ingsw.eriantys.model.characters.MushroomGuy}'s {@link it.polimi.ingsw.eriantys.model.characters.CharacterCard} effect.
 */
public class ExcludeColorInfluence implements InfluenceCalculator {
    private Color excluded;

    public int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors) {
        return 42;

        /*
        int result = 0;

        StudentContainer students = island.getContainer();
        for (Color c : ownedProfessors)
            if (c != excluded)
                result += students.getQuantity(c);
        if (island.getController().equals(player))
            result += island.getNumberOfTowers();
        */
    }
}