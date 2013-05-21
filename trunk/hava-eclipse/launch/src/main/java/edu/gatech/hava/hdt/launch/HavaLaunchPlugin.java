package edu.gatech.hava.hdt.launch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.gatech.hava.hdt.launch.config.MemoryChecker;
import edu.gatech.hava.hdt.launch.event.HavaStartEvent;
import edu.gatech.hava.hdt.launch.event.HavaStopEvent;
import edu.gatech.hava.hdt.launch.event.IHavaLaunchListener;
import edu.gatech.hava.hdt.launch.preferences.PreferenceConstants;

/**
 * The Launch plugin defines the Hava launch configuration,
 * and relevant UI elements.
 */
public class HavaLaunchPlugin extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "edu.gatech.hava.hdt.launch";

    /** The shared instance. */
    private static HavaLaunchPlugin plugin;

    private Set<IHavaLaunchListener> listeners =
        Collections.synchronizedSet(
                new HashSet<IHavaLaunchListener>());

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception {

        super.start(context);
        plugin = this;

    }

    /** {@inheritDoc} */
    @Override
    public void stop(final BundleContext context) throws Exception {

        plugin = null;
        super.stop(context);

    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static HavaLaunchPlugin getDefault() {

        return plugin;

    }

    /**
     * Writes a message to this plugin's log.
     *
     * @param severity the severity; one of the constants in {@link IStatus}
     * @param message a human-readable message
     * @param exception a low-level exception, or <code>null</code>
     */
    public void log(final int severity,
                    final String message,
                    final Throwable exception) {

        final IStatus status = new Status(
                severity, PLUGIN_ID, message, exception);

        getLog().log(status);
    }

    /**
     * Adds a launch listener.
     *
     * @param listener the listener to add
     */
    public void addListener(
            final IHavaLaunchListener listener) {

        listeners.add(listener);

    }

    /**
     * Removes a launch listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(
            final IHavaLaunchListener listener) {

        listeners.remove(listener);

    }

    /**
     * Fires an event indicating that a hava launch has started.
     *
     * @param event the start event
     */
    public void fire(final HavaStartEvent event) {

        synchronized (listeners) {
            for (final IHavaLaunchListener listener : listeners) {
                listener.fire(event);
            }
        }

    }

    /**
     * Fires an event indicating that a hava launch has stopped.
     *
     * @param event the stop event
     */
    public void fire(final HavaStopEvent event) {

        synchronized (listeners) {
            for (final IHavaLaunchListener listener : listeners) {
                listener.fire(event);
            }
        }

    }

    public MemoryChecker getMemoryChecker() {

        final IPreferenceStore preferenceStore =
            getPreferenceStore();

        final int maxMemoryPercentage = preferenceStore.getInt(
            PreferenceConstants.P_MAX_MEMORY);

        return new MemoryChecker(maxMemoryPercentage);

    }

}
