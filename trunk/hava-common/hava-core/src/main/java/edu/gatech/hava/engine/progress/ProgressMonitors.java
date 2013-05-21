package edu.gatech.hava.engine.progress;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of {@link HProgressMonitor} as a collection
 * of zero or more other {@link HProgressMonitor}s.  It can also
 * issue an abort itself, if the {@link #abort()} method is called.
 */
public class ProgressMonitors implements HProgressMonitor {

    private boolean abort = false;

    private String abortMessage;

    private final Set<HProgressMonitor> monitors =
        new HashSet<HProgressMonitor>();

    /**
     * Adds another monitor to the collection.
     *
     * @param monitor a progress monitor to add
     */
    public void addMonitor(final HProgressMonitor monitor) {

        monitors.add(monitor);

    }

    /**
     * Removes a monitor from the collection.
     *
     * @param monitor a progress monitor to remove
     */
    public void removeMonitor(final HProgressMonitor monitor) {

        monitors.remove(monitor);

    }

    /**
     * Causes this monitor to request an abort.
     */
    public void abort() {

        abort = true;
        abortMessage = "Interrupted.";

    }

    /** {@inheritDoc} */
    public boolean checkAbort() {

        for (final HProgressMonitor monitor : monitors) {
            if (monitor.checkAbort()) {
                abortMessage = monitor.getAbortMessage();
                return true;
            }
        }

        return abort;

    }

    /** {@inheritDoc} */
    public String getAbortMessage() {

        return abortMessage;

    }

}
