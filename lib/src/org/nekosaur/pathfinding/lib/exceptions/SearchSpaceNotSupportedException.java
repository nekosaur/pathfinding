package org.nekosaur.pathfinding.lib.exceptions;

public class SearchSpaceNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs with the given throwable
	 * @param t the throwable to throw
	 */
	public SearchSpaceNotSupportedException(Throwable t) {
		super(t);
	}

	/**
	 * Constructs with the given message
	 * @param message the message of the exception
	 */
	public SearchSpaceNotSupportedException(String message) {
		super(message);
	}

	/**
	 * Constructs with the given message and the original throwable cause
	 * @param message the message of the exception
	 * @param t the original throwable
	 */
	public SearchSpaceNotSupportedException(String message, Throwable t) {
		super(message, t);
	}
}