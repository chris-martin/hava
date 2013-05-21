package edu.gatech.hava.hdt.runaction;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;


public class HavaRunObjectAction extends ActionDelegate
        implements IObjectActionDelegate {

    private HavaRunAction havaAction = new HavaRunAction();

    /** {@inheritDoc} */
    @Override
    public void run(final IAction action) {

        havaAction.run();

    }

    /** {@inheritDoc} */
    @Override
    public void selectionChanged(final IAction action,
                                 final ISelection selection) {

        final IFile file = getFile(selection);

        havaAction.setFile(file);

    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(final IAction action,
                              final IWorkbenchPart targetPart) {

    }

    private IFile getFile(final ISelection selection) {

        IFile file = null;

        if (selection instanceof IStructuredSelection) {

            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            final Object selectedElement = structuredSelection.getFirstElement();

            if (selectedElement instanceof IFile) {
                file = (IFile) selectedElement;
            }

        }

        return file;

    }

}
