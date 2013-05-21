package edu.gatech.hava.hdt.views.solution;

import edu.gatech.hava.hdt.views.internal.SortAction;

/**
 * Sort button for the solution view.
 */
public class SolutionSortAction extends SortAction {

    private final SolutionTextViewer viewer;

    /**
     * Constructor.
     *
     * @param viewer the viewer for which this action toggles sorting
     */
    public SolutionSortAction(final SolutionTextViewer viewer) {

        this.viewer = viewer;

    }

    /**
     * Toggles sorting of the solution view.
     */
    @Override
    public void run() {

        viewer.toggleSort();
        viewer.refresh();

    }

}
