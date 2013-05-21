package edu.gatech.hava.engine.exception;

import edu.gatech.hava.engine.HException;

/**
 * An exception which is thrown at various points from the {@link Evaluator}.
 *
 * It is "incomplete" in the sense that it lacks information about the
 * location where the error was encountered.  The
 * {@link edu.gatech.hava.engine.HEngine} catches this exception
 * and re-throws it as an {@link HException}.
 */
public class IncompleteHavaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message an error message to display to the user
     */
    public IncompleteHavaException(final String message) {

        super(message);

    }

    /**
     * Converts this exception to an {@link HException}.
     *
     * @param exEnv the {@link ExceptionEnvironment} which contains
     *              information what part of the code was being
     *              executed when the error was encountered.
     * @return an {@link HException} with the same message as this exception
     */
    public HException complete(final ExceptionEnvironment exEnv) {

        return new HException(getMessage(), exEnv);

    }

}
