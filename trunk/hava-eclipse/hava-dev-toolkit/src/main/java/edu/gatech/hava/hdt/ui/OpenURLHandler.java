package edu.gatech.hava.hdt.ui;

import java.net.MalformedURLException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.gatech.hava.hdt.HavaDevToolkit;

public class OpenURLHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow workbenchWindow =
            workbench.getActiveWorkbenchWindow();

        if (workbenchWindow != null) {

            final IWorkbenchPage page =
                workbenchWindow.getActivePage();

            if (page != null) {
                execute(page, event);
            }

        }

        return null;

    }

    private void execute(final IWorkbenchPage page,
                         final ExecutionEvent event) {

        try {
            URLInput input = new URLInput(
                    event.getParameter("edu.gatech.hava.hdt.urlCommandParameter"),
                    HavaDevToolkit.PLUGIN_ID);
            if (input.exists()) {
                openEditor(page, input);
            } else {
                showErrorDialog(event);
            }
        } catch (final MalformedURLException e) {
            ; // TODO log this
        } catch (final PartInitException e) {
            ; // TODO log this
        }

    }

    private void openEditor(final IWorkbenchPage page,
                            final URLInput input)
            throws PartInitException {

        final String editorId = "edu.gatech.hava.hdt.editors.havaEditor";

        final boolean activate = true;

        final int matchFlags = IWorkbenchPage.MATCH_INPUT;

        page.openEditor(input, editorId, activate, matchFlags);

    }

    private void showErrorDialog(final ExecutionEvent event) {

        final Shell shell = HandlerUtil.getActiveShell(event);

        final String dialogTitle = "Error Opening URL";

        final String message = "Resource at URL was not found.";

        final IStatus status = new Status(Status.ERROR, HavaDevToolkit.PLUGIN_ID,
                "Accessing the resource at the provided URL"
                + " returned some status other than 400 OK.");

        final ErrorDialog dialog = new ErrorDialog(
                shell, dialogTitle, message, status, Status.ERROR);

        dialog.open();

    }

}
