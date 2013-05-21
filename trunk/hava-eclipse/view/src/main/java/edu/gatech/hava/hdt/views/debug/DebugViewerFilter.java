package edu.gatech.hava.hdt.views.debug;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import edu.gatech.hava.hdt.views.filter.IFilter;
import edu.gatech.hava.hdt.views.filter.StringFilter;

/**
 * String-based filter for the debug view.
 */
public class DebugViewerFilter extends ViewerFilter
        implements IFilter<String, DebugTreeNode> {

    private final StringFilter<DebugTreeNode> filter =
        new StringFilter<DebugTreeNode>();

    /** {@inheritDoc} */
    @Override
    public boolean select(final Viewer viewer,
                          final Object parentElement,
                          final Object element) {

        return select((DebugTreeNode) element);

    }

    /** {@inheritDoc} */
    @Override
    public boolean select(final DebugTreeNode node) {

        return filter.select(node);

    }

    /** {@inheritDoc} */
    @Override
    public void setSearch(final String search) {

        filter.setSearch(search);

    }

}
