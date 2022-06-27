package it.polimi.ingsw.eriantys.server.exceptions;

import it.polimi.ingsw.eriantys.server.Server;

/**
 * This exception is thrown by the {@link Server} class and by the classes of the controller package whenever no
 * connection can be retrieved for the desired client.
 */
public class NoConnectionException extends Exception {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public NoConnectionException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 */
	public NoConnectionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method
	 * @param cause the detail cause, which is saved for later retrieval by the {@link #getCause()} method
	 *                 (a {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
	 */
	public NoConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param cause the detail cause, which is saved for later retrieval by the {@link #getCause()} method
	 *                 (a {@code null} value is permitted, and indicates that the cause is nonexistent or unknown)
	 */
	public NoConnectionException(Throwable cause) {
		super(cause);
	}
}
