package edu.gatech.hava.hdt.views.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import edu.gatech.hava.hdt.views.image.IconConfig;

/**
 * Action which toggles sorting.  This abstract class
 * does not provide any run method.
 */
public abstract class SortAction extends Action {

    private static final ImageDescriptor ICON = createIcon();

    /**
     * Constructor.
     */
    public SortAction() {

        super("Sort", Action.AS_CHECK_BOX);

        setToolTipText("Sort");
        setImageDescriptor(ICON);

    }

    private static ImageDescriptor createIcon() {

        final IconConfig iconConfig = new IconConfig("sort");

        final ImageDescriptor imageDescriptor =
            HavaViewPlugin.getDefault().getImageDescriptor(iconConfig);

        return imageDescriptor;

    }

    /**
     * Toggles sorting of something.
     */
    @Override
    public abstract void run();

}
