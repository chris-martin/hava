package edu.gatech.hava.hdt.views.internal;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.gatech.hava.hdt.views.image.IconConfig;

/**
 * The View plugin defines views which are used to view
 * the results of Hava script execution.
 */
public class HavaViewPlugin extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "edu.gatech.hava.hdt.view";

    /** The shared instance. */
    private static HavaViewPlugin plugin;

    private static ImageDescriptor createIcon(final String filename) {

        final String path = "icons" + File.separator + filename;

        return imageDescriptorFromPlugin(PLUGIN_ID, path);

    }

    private static void addIcon(final ImageRegistry registry,
            final String id, final String filename) {

        final ImageDescriptor icon = createIcon(filename);
        registry.put(id, icon);

    }

    /** {@inheritDoc} */
    @Override
    protected void initializeImageRegistry(final ImageRegistry registry) {

        addIcon(registry, "loop", "loop.png");
        addIcon(registry, "function", "function.png");
        addIcon(registry, "branch", "branch.png");
        addIcon(registry, "iteration", "iteration.png");
        addIcon(registry, "jump", "jump-to-code.png");
        addIcon(registry, "comment", "comment.png");
        addIcon(registry, "structure", "structure.gif");
        addIcon(registry, "integer", "integer.png");
        addIcon(registry, "real", "real.png");
        addIcon(registry, "indexed", "indexed.png");
        addIcon(registry, "boolean", "boolean.png");
        addIcon(registry, "generic", "green-dot.png");
        addIcon(registry, "private", "private.png");
        addIcon(registry, "sort", "sort.gif");

        // decorators
        addIcon(registry, "hide", "hide.png");
        addIcon(registry, "error", "error.png");

    }

    /**
     * Retrieves an icon image from this plugin's image registry.
     *
     * @param config an icon configuration object
     * @return the image, as defined by the icon configuration
     */
    public Image getImage(final IconConfig config) {

        final String key = config.getKey();

        Image image = getImageRegistry().get(key);

        if (image == null) {
            final ImageDescriptor baseImage =
                getImageRegistry().getDescriptor(config.getBase());
            if (baseImage == null) {
                throw new IllegalArgumentException(
                        "Invalid image id: " + config.getBase());
            }
            final ImageDescriptor id = IconConfig.createImage(
                    baseImage, config.getOptions());
            getImageRegistry().put(key, id);
            image = getImageRegistry().get(key);
        }

        return image;

    }

    /**
     * Retrieves an icon image descriptor from this plugin's image registry.
     *
     * @param config an icon configuration object
     * @return the image descriptor, as defined by the icon configuration
     */
    public ImageDescriptor getImageDescriptor(final IconConfig config) {

        final String key = config.getKey();

        ImageDescriptor id = getImageRegistry().getDescriptor(key);

        if (id == null) {
            ImageDescriptor baseImage =
                getImageRegistry().getDescriptor(config.getBase());
            if (baseImage == null) {
                throw new IllegalArgumentException(
                        "Invalid image id: " + config.getBase());
            }
            id = IconConfig.createImage(
                    baseImage, config.getOptions());
            getImageRegistry().put(key, id);
        }

        return id;

    }

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
    public static HavaViewPlugin getDefault() {

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

}
