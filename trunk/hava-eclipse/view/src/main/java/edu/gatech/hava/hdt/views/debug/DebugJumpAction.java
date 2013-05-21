package edu.gatech.hava.hdt.views.debug;

import org.eclipse.jface.viewers.StructuredViewer;

import edu.gatech.hava.debug.HDebugObject;
import edu.gatech.hava.debug.HDebugReference;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.hdt.views.jump.JumpLocation;
import edu.gatech.hava.hdt.views.jump.ReferenceJumpLocation;
import edu.gatech.hava.hdt.views.jump.SimpleJumpLocation;
import edu.gatech.hava.hdt.views.jump.StructuredViewerJumpAction;

/**
 * A jump-to-declaration action associated with a viewer which has
 * selectable {@link DebugTreeNode}s.
 */
public class DebugJumpAction
        extends StructuredViewerJumpAction<DebugTreeNode> {

    /**
     * Constructs a {@link DebugJumpAction} for the given viewer.
     *
     * @param viewer a viewer whose items are of type {@link DebugTreeNode}.
     */
    public DebugJumpAction(final StructuredViewer viewer) {

        super(viewer);

    }

    /** {@inheritDoc} */
    @Override
    protected JumpLocation getReference(final DebugTreeNode node) {

        JumpLocation location = null;

        location = getNodeLocation(node);

        if (location == null && node.hasException()) {

            final Exception exception = node.getException();

            if (exception instanceof HException) {

                final HException hException = (HException) exception;

                final int line = hException.getLine();
                final String file = hException.getFile();
                location = new SimpleJumpLocation(line, file);

                DebugTreeNode currentNode = node;
                while (location.getFile() == null
                        && currentNode.getParent() != null) {
                    currentNode = node.getParent();
                    location = getNodeLocation(currentNode);
                }

            }

        }

        return location;

    }

    private JumpLocation getNodeLocation(final DebugTreeNode node) {

        JumpLocation location = null;
        HReference reference = null;

        final HDebugObject debugObject = getDebugObject(node);

        if (debugObject != null) {

            final HDebugReference debugReference =
                (HDebugReference) debugObject;

            reference = debugReference.getReference();

            if (reference != null) {

                location = new ReferenceJumpLocation(reference);

            }

        }

        return location;

    }

    private HDebugObject getDebugObject(DebugTreeNode node) {

        HDebugObject debugObject = null;

        while (node != null) {

            debugObject = node.getValue();

            if (debugObject instanceof HDebugReference) {
                break;
            }

            node = node.getParent();

        }

        return debugObject;

    }

}
