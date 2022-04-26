package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.StudentContainer;

/**
 * This exception is thrown by {@link StudentContainer}'s movement methods to indicate
 * that a movement of students between two containers was not completed.
 * @see StudentContainer
 */
public class NoMovementException extends Exception {	// TODO: rename the exception to FailedMovementException?

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public NoMovementException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 */
	public NoMovementException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 * @param cause the detail cause, which is saved for later retrieval by the {@link #getCause()} method
	 *                 (a {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
	 */
	public NoMovementException(String message, Throwable cause) {
		super(message, cause);
	}
}