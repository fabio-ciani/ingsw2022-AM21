package it.polimi.ingsw.eriantys.model.exceptions;

public class InvalidArgumentException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public InvalidArgumentException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).
     */
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
