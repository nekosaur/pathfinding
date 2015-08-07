package org.nekosaur.pathfinding.gui.business.exceptions;

public class MapDataIncompleteException extends RuntimeException {

	/**
	 * Constructs with the given throwable
	 * @param t the throwable to throw
	 */
	public MapDataIncompleteException(Throwable t) {
		super(t);
	}

	/**
	 * Constructs with the given message
	 * @param message the message of the exception
	 */
	public MapDataIncompleteException(String message) {
		super(message);
	}

	/**
	 * Constructs with the given message and the original throwable cause
	 * @param message the message of the exception
	 * @param t the original throwable
	 */
	public MapDataIncompleteException(String message, Throwable t) {
		super(message, t);
	}
}