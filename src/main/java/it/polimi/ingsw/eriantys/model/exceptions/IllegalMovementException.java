package it.polimi.ingsw.eriantys.model.exceptions;

/**
 * The exception is thrown whenever an illegal parameter referred to a game object movement
 * has been passed to a class or specific method.
 */
public class IllegalMovementException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public IllegalMovementException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
     */
    public IllegalMovementException(String message) {
        super();
    }
}