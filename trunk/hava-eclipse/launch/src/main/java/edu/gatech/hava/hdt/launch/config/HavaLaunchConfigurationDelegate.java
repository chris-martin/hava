package edu.gatech.hava.hdt.launch.config;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;
import edu.gatech.hava.hdt.launch.config.task.DebugTask;
import edu.gatech.hava.hdt.launch.config.task.LaunchTask;
import edu.gatech.hava.hdt.launch.config.task.RunTask;

/**
 * Launch configuration delegate for Hava projects.
 *
 * Supports "run" and "debug" modes, in which the Solution View
 * or Debug View is opened to display the results.
 */
public class HavaLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

    /**
     * The identifier for the Hava launch configuration.
     */
    public static final String HAVA_LAUNCH_CONFIGURATION_ID =
        "edu.gatech.hava.hdt.launchConfiguration";

    /**
     * The name of the {@link ILaunchConfiguration} attribute
     * which holds the name of the Hava file being launched.
     */
    public static final String ATTR_HAVA_FILE = "hava.file";

    /**
     * @param configuration the configuration to launch
     * @param mode the mode in which to launch, one of the mode constants
     *             defined by <code>ILaunchManager</code> -
     *             <code>RUN_MODE</code> or <code>DEBUG_MODE</code>
     * @param launch the launch object to contribute processes and
     *               {@link org.eclipse.debug.core.DebugException}
     *               targets to
     * @param monitor progress monitor, or <code>null</code> progress
     *                monitor (may be <code>null</code>), which can be
     *                used to cancel the launch.
     *
     * @throws CoreException if launching fails
     */
    @Override
    public void launch(final ILaunchConfiguration configuration,
                       final String mode,
                       final ILaunch launch,
                       final IProgressMonitor monitor)
            throws CoreException {

        final String fileName = getFileName(configuration);

        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IFile file = root.getFile(new Path(fileName));

        if (!file.exists()) {
            throw abort("Hava file does not exist.", null);
        }

        final HavaProgressMonitor havaMonitor = new HavaProgressMonitor(monitor);

        final LaunchTask task;

        if (ILaunchManager.RUN_MODE.equals(mode)) {
            task = new RunTask(file, havaMonitor, file.getName());
        } else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
            task = new DebugTask(file, havaMonitor, file.getName());
        } else {
            throw abort("Invalid launch mode \"" + mode + "\".", null);
        }

        task.run();

    }

    private String getFileName(final ILaunchConfiguration configuration)
            throws CoreException {

        final String fileName = configuration.getAttribute(ATTR_HAVA_FILE, (String) null);

        if (fileName == null) {
            abort("Hava file not specified.", null);
        }

        return fileName;

    }

    /**
     * Throws an exception with a new status containing the given message and optional exception.
     *
     * @param message error message
     * @param e underlying exception
     */
    private CoreException abort(final String message,
                                final Throwable e) {

        final Status status = new Status(
                IStatus.ERROR, HavaLaunchPlugin.PLUGIN_ID, 0, message, e);

        return new CoreException(status);

    }

}
