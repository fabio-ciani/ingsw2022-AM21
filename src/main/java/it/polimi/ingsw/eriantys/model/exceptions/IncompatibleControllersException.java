package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.IslandGroup;

/**
 * This exception is thrown by {@link IslandGroup}'s {@code merge} method to indicate that the two specified islands do
 * not share the same controller, and therefore cannot be merged.
 * @see IslandGroup#merge(IslandGroup, IslandGroup)
 */
public class IncompatibleControllersException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public IncompatibleControllersException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method.
	 */
	public IncompatibleControllersException(String message) {
		super(message);
	}
}