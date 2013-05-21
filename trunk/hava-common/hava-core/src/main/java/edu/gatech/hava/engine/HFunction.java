package edu.gatech.hava.engine;

import edu.gatech.hava.engine.exception.IncompleteHavaException;

/**
 * A native (Java) function that may be invoked from Hava source. Several
 * functions are predefined in Hava, and others may be registered with the Engine.
 * An external registered function must implement this interface.
 */
public interface HFunction {

    /**
     * @return the name of this function as it would appear in a Hava
     *         invocation, for example, "cosh".
     */
    String getIdentifier();

    /**
     * Evaluates this function.
     *
     * @param args a list of arguments to the function
     * @return the return value of the evaluated function
     */
    HValue evaluate(HValue args)
        throws IncompleteHavaException;

}
