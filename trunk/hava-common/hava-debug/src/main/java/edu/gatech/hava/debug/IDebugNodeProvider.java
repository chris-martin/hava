package edu.gatech.hava.debug;

import java.util.List;

import edu.gatech.hava.engine.HException;

/**
 * A provider of {@link HDebugObject}s.
 */
public interface IDebugNodeProvider {

    /**
     * @return a list of {@link HDebugObject}s, each representing
     *         some variable which was calculated.
     */
    List<HDebugObject> getTopLevelVariables();

    /**
     * @return true if the Hava execution that produced the debug
     *         objects threw some exception, false otherwise
     */
    boolean hasError();

    /**
     * This returns the variable most closely associated with the
     * exception.  May be null.
     *
     * @return one of the debug objects returned by
     *         {@link #getTopLevelVariables()}
     */
    HDebugReference getErrorVariable();

    /**
     * @return The exception thrown by the Hava engine, if such an
     *         exception exists.  If hasError() returns true, this
     *         must not be null.
     */
    HException getException();

}
