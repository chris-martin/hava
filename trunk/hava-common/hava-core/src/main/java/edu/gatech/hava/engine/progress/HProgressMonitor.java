package edu.gatech.hava.engine.progress;

/**
 * An object which, once registered with an
 * {@link edu.gatech.hava.engine.HEngine},
 * can abort the engine's execution and provide
 * a message explaining the cause of the abort.
 */
public interface HProgressMonitor {

    /**
     * @return true to abort the engine's execution,
     *         false to continue
     */
    boolean checkAbort();

    /**
     * This should only be called if checkAbort() returns true.
     * If checkAbort() returns false, behavior is undefined.
     *
     * @return A human-readable string explaining the cause
     *         of the abort.
     */
    String getAbortMessage();

}
