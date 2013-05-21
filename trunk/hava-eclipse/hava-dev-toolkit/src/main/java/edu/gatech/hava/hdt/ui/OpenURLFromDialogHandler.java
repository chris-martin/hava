package edu.gatech.hava.hdt.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import edu.gatech.hava.hdt.ui.wizards.OpenURLInputDialog;

public class OpenURLFromDialogHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        OpenURLInputDialog dialog = new OpenURLInputDialog(HandlerUtil.getActiveShell(event),
                "Open URL", "Enter the address of the resource:");

        // open a dialog to get a URL - if canceled, do nothing
        if (dialog.open() != InputDialog.OK) {
            return null;
        }

        // otherwise, call our other command with a URL parameter

        dialog.close();

        IWorkbench workbench = PlatformUI.getWorkbench();
        IHandlerService handlers = (IHandlerService) workbench.getService(IHandlerService.class);
        ICommandService commands = (ICommandService) workbench.getService(ICommandService.class);
        if (handlers != null && commands != null) {
            Command openURL = commands.getCommand("edu.gatech.hava.hdt.openURLCommand");
            IParameter url;
            try {
                url = openURL.getParameter("edu.gatech.hava.hdt.urlCommandParameter");
            } catch (final NotDefinedException e) {
                return null;
            }
            Parameterization param = new Parameterization(url, dialog.getValue());
            ParameterizedCommand paramCommand = new ParameterizedCommand(openURL,
                    new Parameterization[] {param});
            try {
                handlers.executeCommand(paramCommand, null);
            } catch (final NotDefinedException e) {
                e.printStackTrace();
            } catch (final NotEnabledException e) {
                e.printStackTrace();
            } catch (final NotHandledException e) {
                e.printStackTrace();
            } // TODO - how should we handle abstruse errors like these?
        }

        return null;
    }

}
