package it.polimi.ingsw.eriantys.model.exceptions;

import it.polimi.ingsw.eriantys.model.Board;
import it.polimi.ingsw.eriantys.model.IslandGroup;

/**
 * This exception is thrown by {@link Board}'s island handling methods to indicate that no island matches the requested id.
 * @see Board#getIsland(String)
 * @see Board#unifyIslands(IslandGroup)
 */
public class IslandNotFoundException extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 */
	public IslandNotFoundException(String message) {
		super(message);
	}
}