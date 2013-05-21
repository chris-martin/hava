package edu.gatech.hava.hdt.runaction;

import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.hdt.views.IHavaSolutionView;
import edu.gatech.hava.hdt.views.IHavaDebugView;

class HavaRunAction {

    private IFile file;
    private IFileEditorInput fileEditorInput;

    private static final String SOLUTION_VIEW =
        "edu.gatech.hava.hdt.views.havaSolution";
    private static final String VARIABLE_TREE_VIEW =
        "edu.gatech.hava.hdt.views.debug";

    void setFile(final IFile file) {

        this.file = file;

        this.fileEditorInput = null;

    }

    void setFile(final IFileEditorInput fileEditorInput) {

        this.fileEditorInput = fileEditorInput;

        this.file = null;

    }

    void run() {

        if (fileEditorInput == null && file != null) {

            fileEditorInput = new FileEditorInput(file);

        }

        if (fileEditorInput != null) {

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

                final IHavaSolutionView solutionView = openSolutionView();
                final IHavaDebugView variableTreeView = openVariableTreeView();

                solutionView.setHavaEngine(engine);
                variableTreeView.setHavaEngine(engine);

            } catch (final CoreException e) {
                ;
            }

        }

    }

    private IHavaSolutionView openSolutionView() throws PartInitException {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow workbenchWindow =
                    workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

        return (IHavaSolutionView) workbenchPage.showView(SOLUTION_VIEW);

    }

    private IHavaDebugView openVariableTreeView() throws PartInitException {

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow workbenchWindow =
                    workbench.getActiveWorkbenchWindow();
        final IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

        return (IHavaDebugView) workbenchPage.showView(VARIABLE_TREE_VIEW);

    }

}
