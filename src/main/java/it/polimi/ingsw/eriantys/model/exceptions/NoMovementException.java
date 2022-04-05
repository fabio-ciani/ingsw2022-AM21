package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.StudentContainer;

/**
 * This exception is thrown by {@link StudentContainer}'s movement methods to indicate
 * that a movement of students between two containers was not completed.
 * @see StudentContainer
 */
public class NoMovementException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public NoMovementException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 * method.
	 */
	public NoMovementException(String message) {
		super(message);
	}
}
