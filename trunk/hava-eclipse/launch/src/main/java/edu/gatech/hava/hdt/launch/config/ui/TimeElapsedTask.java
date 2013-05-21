package edu.gatech.hava.hdt.launch.config.ui;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Class to update the Eclipse status bar with the memory usage and time information.
 *
 */
public class TimeElapsedTask implements Runnable {

    private boolean running = false;
    private long startTime = 0;

    /**
     * Start displaying memory usage and time elapsed since this method call.
     * Frequency defaults to once a second.
     */
    public synchronized void start() {

        start(1000);

    }

    /**
     * Start displaying memory usage and time elapsed since this method call.
     *
     * @param howOften Period that the timer should be executed, in milliseconds.
     */
    public synchronized void start(final int howOften) {

        startTime = System.currentTimeMillis();
        running = true;
        Display.getDefault().timerExec(howOften, this);

    }

    @Override
    public void run() {

        if (running()) {

            // update status bar
            IWorkbenchWindow win =  PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPartSite site = win.getActivePage().getActivePart().getSite();

            IActionBars actionBars = null;

            if (site instanceof IViewSite) {
                actionBars =  ((IViewSite) site).getActionBars();
            } else if (site instanceof IEditorSite) {
                actionBars = ((IEditorSite) site).getActionBars();
            }

            if (actionBars != null) {

                IStatusLineManager statusLineManager =
                     actionBars.getStatusLineManager();
//                IContributionManager tbMan = //actionBars.getToolBarManager();
//                actionBars.getToolBarManager();
//                if(tbMan != null) {
//                    //System.err.println("Not null!");
//                    for ( IContributionItem i : tbMan.getItems())
//                        System.err.println(i.getId());
//                    IContributionItem cont = tbMan.find("edu.gatech.hava.hdt.launch.statusToolbar");
//                    //TimeElapsedWorkbenchWindowContribution cont = (TimeElapsedWorkbenchWindowContribution) tbMan.find("edu.gatech.hava.hdt.ui.timeElapsedControl");
//                    if(cont != null) {
//                        System.err.println("found our toolbar");
//                        //cont.setMemoryUsage(6);
//                    }
//                }
                if (statusLineManager != null) {

                    Runtime runtime = Runtime.getRuntime();

                    long max = runtime.maxMemory(),
                        total = runtime.totalMemory(),
                        free = runtime.freeMemory();

                    int percentUsage = 100 - (int) (100 * (max - total + free) / max);

                    long diff = (System.currentTimeMillis() - startTime) / 1000;

                    int seconds = (int) diff % 60,
                        minutes = (int) (diff / 60) % 60,
                        hours = (int) diff / 3600;

                    String message = (hours > 0 ? hours + " hr, " : "")
                        + (hours + minutes > 0 ? minutes + " min, " : "")
                        + seconds + " sec elapsed, " + percentUsage + "% memory used";

                    statusLineManager.setMessage(message);

                }
            }

            // repeat in a second
            Display.getDefault().timerExec(1000, this);

        }

    }

    /**
     * Synchronized method to signal whether or not thread should continue execution.
     *
     * @return whether the timer should continue
     */
    protected synchronized boolean running() {

        return running;

    }

    /**
     * Stop displaying memory usage and time elapsed.
     */
    public synchronized void stop() {

        running = false;

    }

    public Runnable getStartTask() {

        return new Runnable() {

            @Override
            public void run() {
                start();
            }

        };

    }

    public Runnable getStopTask() {

        return new Runnable() {

            @Override
            public void run() {
                stop();
            }

        };

    }

}
