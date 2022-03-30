package it.polimi.ingsw.eriantys.model.exceptions;

/**
 * Exception thrown when trying to remove a certain item that is no longer there.
 */
public class ItemNotAvailableException extends Exception {
    /**
     * Constructs a new exception with no custom message.
     */
    public ItemNotAvailableException() {
        super();
    }

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message Message with details about what went wrong.
     */
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
