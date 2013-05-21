package edu.gatech.hava.hdt.launch.config.task;

import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import edu.gatech.hava.engine.HAbortException;
import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.progress.HProgressMonitor;
import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;
import edu.gatech.hava.hdt.launch.config.MemoryMonitor;
import edu.gatech.hava.hdt.launch.config.ui.LaunchUITask;
import edu.gatech.hava.hdt.launch.config.ui.TimeElapsedTask;
import edu.gatech.hava.hdt.launch.event.HavaStartEvent;
import edu.gatech.hava.hdt.launch.event.HavaStopEvent;
import edu.gatech.hava.hdt.launch.event.HavaStopEvent.Type;

public abstract class LaunchTask {

    private final IFile file;
    private final HProgressMonitor monitor;
    private final HEngine engine;
    private final LaunchUITask uiTask;
    private final String name;

    LaunchTask(final IFile file,
               final HProgressMonitor monitor,
               final String name) {

        this.file = file;
        this.monitor = monitor;
        this.name = name;

        engine = new HEngine();
        uiTask = new LaunchUITask(file.getName(), engine);

    }

    public void abort(final Throwable t) {

        engine.abort();
        engine.reset();

    }

    public void run() throws CoreException {

        System.gc();

        fireStartEvent();

        engine.addProgressMonitor(monitor);

        Exception loadException = null;

        try {

            engine.setSourceTreeProvider(new HavaSourceTreeProvider());

            final String filePath = file.getFullPath().toPortableString();
            engine.reset(filePath);

            engine.load(new InputStreamReader(file.getContents()));

        } catch (final HException e) {

            loadException = e;

        } catch (final IOException e) {

            loadException = e;

        }

        final TimeElapsedTask t = new TimeElapsedTask();

        beforeRun();

        if (loadException != null) {

            uiTask.addLoadException(loadException, null);

        } else {

            final MemoryMonitor memoryMonitor =
                new MemoryMonitor();
            engine.addDebugListener(memoryMonitor);
            engine.addProgressMonitor(memoryMonitor);

            try {
                Display.getDefault().asyncExec(t.getStartTask());
                engine.run();
                fireStopEvent(Type.SUCCESS);
            } catch (final HAbortException e) {
                fireStopEvent(Type.ABORT);
            } catch (final HException e) {
                fireStopEvent(Type.FAIL);
                uiTask.addRuntimeException(e, null);
            } finally {
                Display.getDefault().asyncExec(t.getStopTask());
            }

        }

        afterRun();

        Display.getDefault().asyncExec(uiTask);

    }

    /**
     * @return the mode of this launch (run or debug)
     */
    protected abstract HavaStartEvent.Mode getMode();

    /**
     * Called after the Hava code is parsed, but
     * before it has been run.
     */
    protected void beforeRun() { }

    /**
     * Called after the Hava code has been run.
     */
    protected void afterRun() { }

    protected HEngine getEngine() {

        return engine;

    }

    protected LaunchUITask getUITask() {

        return uiTask;

    }

    private void fireStartEvent() {

        final HavaStartEvent event =
            new HavaStartEvent(name, getMode());

        HavaLaunchPlugin.getDefault().fire(event);

    }

    private void fireStopEvent(final Type type) {

        final HavaStopEvent event =
            new HavaStopEvent(name, type);

        HavaLaunchPlugin.getDefault().fire(event);

    }

}
