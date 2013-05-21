package edu.gatech.hava.hdt.runaction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PerspectiveAdapter;

/**
 * Editor contribution for the Hava editor which adds a toolbar button,
 * to run the current Hava file.
 */
public class HavaRunEditorAction implements IWorkbenchWindowActionDelegate {

    private HavaRunAction havaAction = new HavaRunAction();

    /** {@inheritDoc} */
    @Override
    public void run(final IAction action) {

        havaAction.run();

    }

    /** {@inheritDoc} */
/*
    @Override
    public void setActiveEditor(final IAction action,
                                final IEditorPart targetEditor) {

        final IFileEditorInput fileEditorInput = getFileEditorInput(targetEditor);

        havaAction.setFile(fileEditorInput);

    }
*/

    /** {@inheritDoc} */
    @Override
    public void selectionChanged(final IAction action,
                                 final ISelection selection) {

        System.out.println(selection.getClass());

    }

    private IFileEditorInput getFileEditorInput(final IEditorPart editorPart) {

        IFileEditorInput fileEditorInput = null;

        final IEditorInput editorInput = editorPart.getEditorInput();

        if (editorInput instanceof IFileEditorInput) {
            fileEditorInput = (IFileEditorInput) editorInput;
        }

        return fileEditorInput;

    }

    @Override
    public void dispose() {

        window.removePerspectiveListener(listener);

    }

    private IWorkbenchWindow window;
    private IPerspectiveListener listener;

    @Override
    public void init(final IWorkbenchWindow window) {

        listener = new PerspectiveAdapter() {

        };

        window.addPerspectiveListener(listener);

    }

}
