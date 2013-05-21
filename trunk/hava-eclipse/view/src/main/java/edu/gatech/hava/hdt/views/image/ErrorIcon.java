package edu.gatech.hava.hdt.views.image;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * A composite image formed by drawing a red x atop a base image
 * in the bottom-left corner.
 */
class ErrorIcon extends CompositeImageDescriptor {

    private Image baseImage;

    private Point size;

    private final HavaViewPlugin viewPlugin = HavaViewPlugin.getDefault();

    /**
     * @param baseImage the base image
     */
    public ErrorIcon(final Image baseImage) {

        this.baseImage = baseImage;

        size = new Point(baseImage.getBounds().width,
                         Math.max(16, baseImage.getBounds().height));

    }

    /** {@inheritDoc} */
    @Override
    protected void drawCompositeImage(final int width, final int height) {

        drawImage(baseImage.getImageData(), 0, 0);

        final Image errorImage = viewPlugin.getImage(new IconConfig("error"));
        final ImageData overlayImageData = errorImage.getImageData();

        drawImage(overlayImageData, 0, size.y - errorImage.getBounds().height);

    }

    /** {@inheritDoc} */
    @Override
    protected Point getSize() {

        return size;

    }

}
