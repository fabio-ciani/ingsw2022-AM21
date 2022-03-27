package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.StudentContainer;

/**
 * This exception is thrown by {@link it.polimi.ingsw.eriantys.model.StudentContainer}'s movement methods to indicate
 * that a movement of students between two containers was not completed.
 * @see StudentContainer
 */
public class NoMovementException extends Exception {

	public NoMovementException() {
		super();
	}

	public NoMovementException(String message) {
		super(message);
	}
}
