package edu.gatech.hava.hdt.launch.config;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.gatech.hava.engine.progress.HProgressMonitor;

/**
 * A simple implementation of {@link HProgressMonitor} which
 * wraps an {@link IProgressMonitor}.
 *
 * When the {@link IProgressMonitor} is canceled, this monitor
 * aborts the Hava execution.
 */
class HavaProgressMonitor implements HProgressMonitor {

    private final IProgressMonitor monitor;

    /**
     * Constructor.
     *
     * @param monitor an {@link IProgressMonitor} which controls
     *                the aborting of this monitor
     */
    public HavaProgressMonitor(final IProgressMonitor monitor) {

        this.monitor = monitor;

    }

    /** {@inheritDoc} */
    @Override
    public boolean checkAbort() {

        return monitor.isCanceled();

    }

    @Override
    public String getAbortMessage() {

        return "Interrupted.";

    }

}
