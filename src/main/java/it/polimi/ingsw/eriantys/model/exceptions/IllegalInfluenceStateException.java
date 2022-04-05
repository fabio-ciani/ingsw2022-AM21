package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.influence.InfluenceCalculator;

/**
 * The exception is thrown whenever an illegal parameter has been passed to
 * the state pattern {@link InfluenceCalculator} class.
 */
public class IllegalInfluenceStateException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public IllegalInfluenceStateException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public IllegalInfluenceStateException(String message) {
        super(message);
    }
}