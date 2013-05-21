package edu.gatech.hava.hdt.views.image;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An image specification which consists of the key for a base image,
 * and some optional decorators.
 */
public class IconConfig {

    /**
     * "Hide" decorator - draws a slash over the image.
     */
    public static final int ICON_HIDE = 1;

    /**
     * "Error" decorator - draws a red x in the corner.
     */
    public static final int ICON_ERROR = 2;

    private String base = null;

    private int options = 0;

    /**
     * Constructs an {@link IconConfig} without specifying a base image
     * and with no options.
     */
    public IconConfig() {

    }

    /**
     * Constructs an {@link IconConfig} with the given base image
     * and no options.
     *
     * @param base the key of the base image
     */
    public IconConfig(final String base) {

        this.base = base;

    }

    /**
     * Constructs an {@link IconConfig} with the given base image
     * and some options.
     *
     * @param base the key of the base image
     * @param options a binary OR combination of option constants
     */
    public IconConfig(final String base, final int options) {

        this.base = base;
        this.options = options;

    }

    public String getBase() {

        return base;

    }

    public void setBase(final String base) {

        this.base = base;

    }

    public int getOptions() {

        return options;

    }

    /**
     * Turns on an option.
     *
     * @param option one of the option constants
     */
    public void addOption(final int option) {

        options |= option;

    }

    /**
     * @return the full name of this image's key in the image registry
     *
     * @throws IllegalStateException if <tt>base</tt> is not set
     */
    public String getKey() {

        if (base == null) {
            throw new IllegalStateException("Base image is not set.");
        }

        final StringBuilder str = new StringBuilder(base);

        if ((options / ICON_HIDE) % 2 == 1) {
            str.append(".hide");
        }
        if ((options / ICON_ERROR % 2 == 1)) {
            str.append(".error");
        }

        return str.toString();

    }

    /**
     * Applies the given options to the given image.
     *
     * @param image a base image to modify
     * @param options any options to apply
     * @return the new image
     */
    public static ImageDescriptor createImage(ImageDescriptor image,
            final int options) {

        if ((options / ICON_HIDE) % 2 == 1) {
            image = new HideIcon(image.createImage());
        }

        if ((options / ICON_ERROR) % 2 == 1) {
            image = new ErrorIcon(image.createImage());
        }

        return image;

    }

}
