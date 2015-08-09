package org.nekosaur.pathfinding.lib.exceptions;

public class MissingMapDataException extends RuntimeException {

	/**
	 * Constructs with the given throwable
	 * @param t the throwable to throw
	 */
	public MissingMapDataException(Throwable t) {
		super(t);
	}

	/**
	 * Constructs with the given message
	 * @param message the message of the exception
	 */
	public MissingMapDataException(String message) {
		super(message);
	}

	/**
	 * Constructs with the given message and the original throwable cause
	 * @param message the message of the exception
	 * @param t the original throwable
	 */
	public MissingMapDataException(String message, Throwable t) {
		super(message, t);
	}
}