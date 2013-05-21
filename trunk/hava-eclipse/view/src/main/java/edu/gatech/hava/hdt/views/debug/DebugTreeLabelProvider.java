package edu.gatech.hava.hdt.views.debug;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.gatech.hava.debug.HDebugConditional;
import edu.gatech.hava.debug.HDebugFunction;
import edu.gatech.hava.debug.HDebugIteration;
import edu.gatech.hava.debug.HDebugObject;
import edu.gatech.hava.debug.HDebugReference;
import edu.gatech.hava.debug.HDebugSimpleNode;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.hdt.views.image.IconConfig;
import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * Maps each {@link DebugTreeNode} to an icon image and text string
 * which visually represents it in a viewer.
 */
class DebugTreeLabelProvider extends StyledCellLabelProvider {

    private static final Color MAIN_FOREGROUND =
        Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

    private static final Color VALUE_FOREGROUND =
        Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);

    private static final Color ERROR_FOREGROUND =
        Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);

    public String getText(final Object element) {

        if (element instanceof DebugTreeNode) {

            final DebugTreeNode node = (DebugTreeNode) element;

            return node.toString() + getValueText(node);

        } else {

            HavaViewPlugin.getDefault().log(IStatus.ERROR,
                    "Illegal element type " + element.getClass().toString(),
                    new ClassCastException());

            return "";

        }

    }

    /**
     * Updates the label for a cell.
     *
     * @param cell the cell to update - its element must
     *             be a {@link DebugTreeNode}.
     */
    @Override
    public void update(final ViewerCell cell) {

        final Object element = cell.getElement();

        if (element instanceof DebugTreeNode) {

            final DebugTreeNode node = (DebugTreeNode) element;

            // Set the cell's icon image
            cell.setImage(getImage(node));

            // Set the cell's text
            final String text = node.toString();
            final String valueText = getValueText(node);
            cell.setText(text + valueText);

            // Set the cell's styles
            final Color valueColor = node.hasException()
                ? ERROR_FOREGROUND : VALUE_FOREGROUND;
            cell.setStyleRanges(new StyleRange[]{
                new StyleRange(0, text.length(), MAIN_FOREGROUND, null),
                new StyleRange(text.length(), valueText.length(), valueColor, null)
            });

        } else {

            HavaViewPlugin.getDefault().log(IStatus.ERROR,
                "Illegal element type " + element.getClass().toString(),
                new ClassCastException());

        }

    }

    private Image getImage(final DebugTreeNode node) {

        Image image;
        if (node.getValue() == null) {
            image = PlatformUI.getWorkbench().getSharedImages().getImage(
                    ISharedImages.IMG_OBJS_ERROR_TSK);
        } else {
            final IconConfig config = getIconConfig(node);
            image = HavaViewPlugin.getDefault().getImage(config);
        }

        return image;

    }

    private IconConfig getIconConfig(final DebugTreeNode node) {

        final IconConfig config = new IconConfig();
        final HDebugObject debugObject = node.getValue();

        HValue value = debugObject.getValue();

        if (debugObject instanceof HDebugSimpleNode) {
            config.setBase("loop");
        } else if (debugObject instanceof HDebugFunction) {
            config.setBase("function");
        } else if (debugObject instanceof HDebugConditional) {
            config.setBase("branch");
        } else if (debugObject instanceof HDebugIteration) {
            config.setBase("iteration");
        } else {
            boolean isPrivate = false;
            if (debugObject instanceof HDebugReference) {
                final HDebugReference debugRef = (HDebugReference) debugObject;
                isPrivate = debugRef.getReference().isPrivate();
            }
            config.setBase(isPrivate ? "private" : "generic");
        }

        if (((debugObject instanceof HDebugReference) && value == null) || debugObject.hasException()) {
            config.addOption(IconConfig.ICON_ERROR);
        }

        return config;

    }

    private String getValueText(final DebugTreeNode node) {

        if (node.getValue() == null) {
            return "";
        }

        final HDebugObject debugObject = node.getValue();

        final String text;

        final HValue value = debugObject.getValue();

        if (value == null) {
            text = "";
        } else {
            text = " = " + value.toString();
        }

        return text;

    }

}
