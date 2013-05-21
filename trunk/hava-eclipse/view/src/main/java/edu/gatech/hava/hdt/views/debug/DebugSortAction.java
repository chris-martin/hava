package edu.gatech.hava.hdt.views.debug;

import org.eclipse.jface.viewers.TreeViewer;

import edu.gatech.hava.hdt.views.internal.SortAction;

/**
 * Sort button for the debug view.
 */
public class DebugSortAction extends SortAction {

    private final TreeViewer viewer;

    private final DebugViewerComparator comparator =
        new DebugViewerComparator();

    /**
     * Constructor.
     *
     * @param viewer the viewer for which this action toggles sorting
     */
    public DebugSortAction(final TreeViewer viewer) {

        this.viewer = viewer;

    }

    /**
     * Toggles sorting of the debug view.
     */
    @Override
    public void run() {

        if (viewer.getComparator() == null) {
            viewer.setComparator(comparator);
        } else {
            viewer.setComparator(null);
        }

        viewer.refresh();

    }

}
