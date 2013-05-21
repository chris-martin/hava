package edu.gatech.hava.hdt.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import edu.gatech.hava.hdt.launch.config.HavaLaunchConfigurationDelegate;

/**
 * Hava launch shortcut, capable of launching a selection
 * or active editor in the workbench.
 */
public class HavaLaunchShortcut implements ILaunchShortcut {

    /** {@inheritDoc} */
    @Override
    public void launch(final ISelection selection, final String mode) {

        if (selection instanceof IStructuredSelection) {

            final IStructuredSelection structuredSelection =
                (IStructuredSelection) selection;

            if (structuredSelection.size() == 1) {

                final Object element = structuredSelection.getFirstElement();

                if (element instanceof IFile) {

                    final IFile file = (IFile) element;

                    launch(file, mode);

                }

            }

        }

    }

    /** {@inheritDoc} */
    @Override
    public void launch(final IEditorPart editor, final String mode) {

        final IEditorInput editorInput = editor.getEditorInput();

        if (editorInput instanceof IFileEditorInput) {

            final IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;

            final IFile file = fileEditorInput.getFile();

            launch(file, mode);

        }

    }

    private void launch(final IFile file, final String mode) {

        final ILaunchConfiguration configuration = getConfiguration(file);

        if (configuration != null) {
            DebugUITools.launch(configuration, mode);
        }

    }

    private ILaunchManager getLaunchManager() {

        return DebugPlugin.getDefault().getLaunchManager();

    }

    private ILaunchConfigurationType getLaunchType() {

        final ILaunchManager manager = getLaunchManager();
        final ILaunchConfigurationType type = manager.getLaunchConfigurationType(
                HavaLaunchConfigurationDelegate.HAVA_LAUNCH_CONFIGURATION_ID);
        return type;

    }

    private ILaunchConfiguration[] getLaunchConfigurations() throws CoreException {

        return getLaunchManager().getLaunchConfigurations(getLaunchType());

    }

    private ILaunchConfiguration getConfiguration(final IFile file) {

        final List<ILaunchConfiguration> candidates = new ArrayList<ILaunchConfiguration>();
        try {

            final ILaunchConfiguration[] configurations = getLaunchConfigurations();
            for (final ILaunchConfiguration configuration : configurations) {
                IResource[] resources = configuration.getMappedResources();
                if (resources != null && resources.length == 1
                        && resources[0].equals(file)) {
                    candidates.add(configuration);
                }
            }

        } catch (final CoreException e) {
            ;
        }

        if (!candidates.isEmpty()) {
            return chooseConfiguration(candidates);
        }

        return newConfiguration(file);


    }

    private ILaunchConfiguration newConfiguration(final IFile file) {

        ILaunchConfigurationType type = getLaunchType();

        try {

            ILaunchConfigurationWorkingCopy workingCopy =
                type.newInstance(null, defaultConfigurationName(file));

            workingCopy.setAttribute(
                    HavaLaunchConfigurationDelegate.ATTR_HAVA_FILE,
                    file.getFullPath().toString());

            workingCopy.setMappedResources(new IResource[]{file});

            return workingCopy.doSave();

        } catch (final CoreException e) {
            e.printStackTrace();
        }

        return null;

    }

    private String defaultConfigurationName(final IFile file) {

        return getLaunchManager().generateUniqueLaunchConfigurationNameFrom(
                "[" + file.getProject().getName() + "] " + file.getName());

    }

    /**
     * If multiple resources are selected, this picks one of them
     * to run (just returns the first element in the list).
     */
    private ILaunchConfiguration chooseConfiguration(
            final List<ILaunchConfiguration> configList) {

        return configList.get(0);

    }

}
