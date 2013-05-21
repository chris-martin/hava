package edu.gatech.hava.hdt.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This is a sample new wizard. Its role is to create a new file
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * ".hava". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */
public class NewHavaFileWizard extends Wizard implements INewWizard {

    private HavaFileWizardPage page;
    private ISelection selection;

    /**
     * Constructor for NewHavaFileWizard.
     */
    public NewHavaFileWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {
        page = new HavaFileWizardPage(selection);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {

        final String containerName = page.getContainerName();
        final String fileName = page.getFileName();

        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(final IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
                } catch (final CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            getContainer().run(true, false, op);
        } catch (final InterruptedException e) {
            return false;
        } catch (final InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;

    }

    /**
     * The worker method. It will find the container, create the
     * file if missing or just replace its contents, and open
     * the editor on the newly created file.
     */

    private void doFinish(final String containerName,
                          final String fileName,
                          final IProgressMonitor monitor)
            throws CoreException {

        // create a sample file
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Container \"" + containerName + "\" does not exist.");
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        try {
            InputStream stream = openContentStream();
            if (file.exists()) {
                file.setContents(stream, true, true, monitor);
            } else {
                file.create(stream, true, monitor);
            }
            stream.close();
        } catch (final IOException e) {
            ;
        }

        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");

        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                final IWorkbenchPage page =
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    IDE.openEditor(page, file, true);
                } catch (final PartInitException e) {
                    ;
                }
            }
        });

        monitor.worked(1);

    }

    /**
     * We will initialize file contents with a sample text.
     */

    private InputStream openContentStream() {

        final String contents = "";

        return new ByteArrayInputStream(contents.getBytes());

    }

    private void throwCoreException(final String message) throws CoreException {

        final IStatus status =
            new Status(IStatus.ERROR, "edu.gatech.hava.hdt", IStatus.OK, message, null);

        throw new CoreException(status);

    }

    /**
     * We will accept the selection in the workbench to see if
     * we can initialize from it.
     *
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench,
                     final IStructuredSelection selection) {

        this.selection = selection;

    }

}
