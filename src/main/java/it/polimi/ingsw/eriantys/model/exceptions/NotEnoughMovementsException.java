package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.GameManager;

/**
 * This exception is thrown by {@link GameManager#handleMotherNatureMovement(String)} to indicate that the movement
 * cannot be completed since the player does not have enough MN movements during this turn.
 */
public class NotEnoughMovementsException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public NotEnoughMovementsException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 */
	public NotEnoughMovementsException(String message) {
		super(message);
	}
}
