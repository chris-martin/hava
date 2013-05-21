package edu.gatech.hava.hdt.ui.wizards;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import edu.gatech.hava.hdt.builder.HavaBuilder;

public class NewHavaProjectWizard extends Wizard implements INewWizard{

    private HavaProjectWizardPage namePage;

    @Override
    public boolean performFinish() {

        if (namePage != null) {

            IProjectDescription desc = ResourcesPlugin.getWorkspace().
                newProjectDescription(namePage.getValue());
            desc.setNatureIds(new String[]{"edu.gatech.hava.hdt.nature"});
            ICommand buildCommand = desc.newCommand();
            buildCommand.setBuilderName(HavaBuilder.BUILDER_ID);
            desc.setBuildSpec(new ICommand[]{buildCommand});

            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(desc.getName());

            IProgressMonitor progressMonitor = new NullProgressMonitor();

            try {
                project.create(desc, progressMonitor);
                project.open(progressMonitor);
            } catch (final CoreException e) {
                namePage.setMessage(e.getMessage());
                e.printStackTrace();
                return false;
            }

            return true;

        }

        return false;

    }

    @Override
    public void init(final IWorkbench workbench,
                     final IStructuredSelection selection) {

    }

    public void addPages() {

        setWindowTitle("New Hava Project");
        namePage = new HavaProjectWizardPage();
        addPage(namePage);

    }

}
