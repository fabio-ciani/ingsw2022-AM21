package it.polimi.ingsw.eriantys.model;

import java.util.Set;

/**
 * The influence calculation during the game is operated by a calculator entity.
 * This interface encloses a state pattern, which will be implemented by concrete classes.
 */
public interface InfluenceCalculator {
    public abstract int calculate(Player player, IslandGroup island, Set<Color> ownedProfessors);

    // TODO: Should we change it to an abstract class with calculate() as an implementation of NoEffectInfluence?
}