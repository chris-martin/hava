package edu.gatech.hava.hdt.launch.config;

import edu.gatech.hava.engine.debug.HDebugAdapter;
import edu.gatech.hava.engine.progress.HProgressMonitor;
import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;

/**
 * {@link MemoryMonitor} is an {@link HProgressMonitor} which
 * periodically detects the percentage of available memory,
 * and aborts if memory usage is too high.
 *
 * It is implemented as an {@HDebugListener} so that it can run
 * synchronously with a Hava engine.
 */
public class MemoryMonitor extends HDebugAdapter
        implements HProgressMonitor {

    private static final int MEMORY_CHECK_PERIOD = 100;

    private int i = 0;

    private boolean abort = false;

    private final MemoryChecker memoryChecker;

    /**
     * Constructor.
     */
    public MemoryMonitor() {

        memoryChecker =
            HavaLaunchPlugin.getDefault().getMemoryChecker();

    }

    /** {@inheritDoc} */
    @Override
    public boolean checkAbort() {

        return abort;

    }

    /** {@inheritDoc} */
    @Override
    public String getAbortMessage() {

        return "Out of memory.";

    }

    /** {@inheritDoc} */
    @Override
    protected void debugEvent() {

        if (i == MEMORY_CHECK_PERIOD) {
            i = 0;
            if (memoryChecker.usageExceedsMaximum()) {
                abort = true;
            }
        }

        i++;

    }

}
