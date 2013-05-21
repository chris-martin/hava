package edu.gatech.hava.hdt.views.debug;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * Comparator for alphanumerically sorting the debug view.
 */
public class DebugViewerComparator extends ViewerComparator {

    /** {@inheritDoc} */
    @Override
    public int compare(final Viewer viewer,
                       final Object e1,
                       final Object e2) {

        DebugTreeNode n1 = (DebugTreeNode) e1;
        DebugTreeNode n2 = (DebugTreeNode) e2;

        return compare(n1, n2);

    }

    private int compare(final DebugTreeNode n1,
                        final DebugTreeNode n2) {

        // only care about root-level nodes
        if (n1.getParent() != null && n2.getParent() != null) {
            return 0;
        }

        return str(n1).compareToIgnoreCase(str(n2));

    }

    private String str(final Object obj) {

        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }

    }

}
