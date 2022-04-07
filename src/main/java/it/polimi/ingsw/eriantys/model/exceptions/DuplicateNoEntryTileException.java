package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.IslandGroup;

/**
 * This exception is thrown by {@link IslandGroup#putNoEntryTile(int)} to indicate that a tile with the specified
 * {@code id} is already placed on the {@link IslandGroup}.
 * @see IslandGroup#putNoEntryTile(int)
 */
public class DuplicateNoEntryTileException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public DuplicateNoEntryTileException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 */
	public DuplicateNoEntryTileException(String message) {
		super(message);
	}
}