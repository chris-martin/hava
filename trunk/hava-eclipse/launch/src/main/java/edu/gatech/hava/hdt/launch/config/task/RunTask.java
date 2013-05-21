package edu.gatech.hava.hdt.launch.config.task;

import org.eclipse.core.resources.IFile;

import edu.gatech.hava.engine.progress.HProgressMonitor;
import edu.gatech.hava.hdt.launch.event.HavaStartEvent.Mode;

public class RunTask extends LaunchTask {

    private RunDebugListener debugListener;

    public RunTask(final IFile file,
                   final HProgressMonitor monitor,
                   final String name) {

        super(file, monitor, name);

    }

    @Override
    protected void beforeRun() {

        debugListener = new RunDebugListener();

        getEngine().addDebugListener(debugListener);
        getUITask().setDebugNodeProvider(debugListener);

    }

    @Override
    protected Mode getMode() {

        return Mode.RUN;

    }

}
