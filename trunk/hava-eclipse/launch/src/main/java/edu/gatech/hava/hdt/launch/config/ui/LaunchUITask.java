package edu.gatech.hava.hdt.launch.config.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.gatech.hava.debug.IDebugNodeProvider;
import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;
import edu.gatech.hava.hdt.views.IHavaDebugView;
import edu.gatech.hava.hdt.views.IHavaSolutionView;

public class LaunchUITask implements Runnable {

    private final HEngine engine;
    private IDebugNodeProvider debugNodeProvider;
    private Exception exception;
    private ExceptionType exceptionType;
    private HReference exRef;
    private String codeIdentifier;

    public LaunchUITask(String havaID, final HEngine engine) {

        this.codeIdentifier = havaID;
        this.engine = engine;

    }

    public LaunchUITask(final HEngine engine) {

        this.codeIdentifier = null;
        this.engine = engine;

    }

    public void setDebugNodeProvider(
            final IDebugNodeProvider debugNodeProvider) {

        this.debugNodeProvider = debugNodeProvider;

    }

    public void addLoadException(final Exception exception, final HReference exRef) {

        this.exception = exception;
        this.exRef = exRef;
        this.exceptionType = ExceptionType.Load;
    }

    public void addRuntimeException(final HException exception, final HReference exRef) {

        this.exception = exception;
        this.exRef = exRef;
        this.exceptionType = ExceptionType.Runtime;
    }

    @Override
    public void run() {

        try {

            final IHavaSolutionView solutionView = openSolutionView();
            solutionView.setContent(codeIdentifier, engine, exception, exRef);

        } catch (final PartInitException pie) {
            HavaLaunchPlugin.getDefault().log(IStatus.ERROR,
                    "Failed to launch solution view.", pie);
        }

        try {

            final IHavaDebugView debugView = openDebugView();
            if (exception != null && ExceptionType.Load == exceptionType) {
                debugView.setContent(codeIdentifier, debugNodeProvider, exception);
            } else {
                debugView.setContent(codeIdentifier, debugNodeProvider, null);
            }

        } catch (final PartInitException pie) {
            HavaLaunchPlugin.getDefault().log(IStatus.ERROR,
                    "Failed to launch debug view.", pie);
        }

    }

    private IHavaSolutionView openSolutionView() throws PartInitException {

        final IViewPart viewPart = showView(IHavaSolutionView.VIEW_ID);

        return (IHavaSolutionView) viewPart;

    }

    private IHavaDebugView openDebugView() throws PartInitException {

        final IViewPart viewPart = showView(IHavaDebugView.VIEW_ID);

        return (IHavaDebugView) viewPart;

    }

    private IWorkbenchPage getWorkbenchPage() {

        final IWorkbench workbench = PlatformUI.getWorkbench();

        final IWorkbenchWindow workbenchWindow =
            workbench.getActiveWorkbenchWindow();

        final IWorkbenchPage workbenchPage =
            workbenchWindow.getActivePage();

        return workbenchPage;

    }

    private IViewPart showView(final String id)
            throws PartInitException {

        return getWorkbenchPage().showView(id, null, IWorkbenchPage.VIEW_CREATE);

    }

    private enum ExceptionType
    {
        Load,
        Runtime
    }
}
