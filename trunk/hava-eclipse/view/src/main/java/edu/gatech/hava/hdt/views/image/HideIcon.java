package edu.gatech.hava.hdt.views.image;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * A composite image formed by drawing a slash atop a base image.
 */
class HideIcon extends CompositeImageDescriptor {

    private Image baseImage;

    private Point size;

    private final HavaViewPlugin viewPlugin = HavaViewPlugin.getDefault();

    /**
     * @param baseImage the base image
     */
    public HideIcon(final Image baseImage) {

        this.baseImage = baseImage;

        size = new Point(baseImage.getBounds().width,
                         baseImage.getBounds().height);

    }

    /** {@inheritDoc} */
    @Override
    protected void drawCompositeImage(final int width, final int height) {

        drawImage(baseImage.getImageData(), 0, 0);

        ImageData overlayImageData = viewPlugin.getImage(new IconConfig("hide")).getImageData();

        drawImage(overlayImageData, 0, 0);

    }

    /** {@inheritDoc} */
    @Override
    protected Point getSize() {

        return size;

    }

}
