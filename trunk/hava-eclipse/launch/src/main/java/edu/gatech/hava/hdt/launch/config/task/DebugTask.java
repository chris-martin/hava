package edu.gatech.hava.hdt.launch.config.task;

import org.eclipse.core.resources.IFile;

import edu.gatech.hava.debug.HDebugBase;
import edu.gatech.hava.engine.progress.HProgressMonitor;
import edu.gatech.hava.hdt.launch.event.HavaStartEvent.Mode;

public class DebugTask extends LaunchTask {

    public DebugTask(final IFile file,
                     final HProgressMonitor monitor,
                     final String name) {

        super(file, monitor, name);

    }

    @Override
    protected void beforeRun() {

        final HDebugBase debugListener = new HDebugBase();

        getEngine().addDebugListener(debugListener);
        getUITask().setDebugNodeProvider(debugListener);

    }

    @Override
    protected Mode getMode() {

    	return Mode.DEBUG;

    }

}
