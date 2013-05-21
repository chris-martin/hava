package edu.gatech.hava.hdt.runaction;

import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.part.FileEditorInput;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.hdt.views.IHavaSolutionView;

public class OpenHavaResultDelegate extends ActionDelegate
        implements IObjectActionDelegate {

    private static final String SOLUTION_VIEW =
        "edu.gatech.hava.hdt.views.HavaSolutionView";

    private IFile file;

    @Override
    public void run(final IAction action) {
        IFileEditorInput fileEditorInput = new FileEditorInput(file);

        final HEngine engine = new HEngine();

        try {
            engine.load(new InputStreamReader(fileEditorInput.getFile().getContents()));
            engine.run();
        } catch (final HException e) {
            ;
        } catch (final IOException e) {
            ;
        } catch (final CoreException e) {
            ;
        }

        try {

            IHavaSolutionView solutionView = openSolutionView();

            solutionView.setHavaEngine(engine);

        } catch (final CoreException e) {
            ;
        }

    }

    @Override
    public void setActivePart(final IAction action,
                              final IWorkbenchPart targetPart) {

    }

    @Override
    public void selectionChanged(final IAction action,
                                 final ISelection selection) {

        file = (IFile) (((IStructuredSelection) selection).getFirstElement());

    }

    private IHavaSolutionView openSolutionView() throws PartInitException {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow workbenchWindow =
                    workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

        return (IHavaSolutionView) workbenchPage.showView(SOLUTION_VIEW);

    }

}
