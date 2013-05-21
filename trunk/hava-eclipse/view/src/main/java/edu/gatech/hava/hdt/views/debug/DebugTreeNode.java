package edu.gatech.hava.hdt.views.debug;

import edu.gatech.hava.debug.HDebugObject;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.hdt.views.internal.TreeNode;

/**
 * A tree node which represents an {@link HDebugObject}.
 */
public class DebugTreeNode
        extends TreeNode<DebugTreeNode, HDebugObject> {

    private static final DebugTreeNode[] ARRAY = new DebugTreeNode[0];
    private Exception exception;

    /**
     * Constructor.
     *
     * @param exception a hava exception represented by this node
     */
    public DebugTreeNode(final Exception exception) {

        super(null, ARRAY);

        this.exception = exception;

    }

    /**
     * Constructor.
     *
     * @param parent this node's parent
     * @param exception a hava exception represented by this node
     */
    public DebugTreeNode(final DebugTreeNode parent, final HException exception) {

        super(parent, null, ARRAY);

        this.exception = exception;

    }

    /**
     * Constructs a new {@link DebugTreeNode} with no parent.
     *
     * @param debugVariable the debug variable represented by this node
     */
    public DebugTreeNode(final HDebugObject debugVariable) {

        super(debugVariable, ARRAY);

    }

    /**
     * Constructs a new {@link DebugTreeNode} with a given parent.
     *
     * @param parent this node's parent
     * @param value the debug variable represented by this node
     */
    public DebugTreeNode(final DebugTreeNode parent,
                             final HDebugObject value) {

        super(parent, value, ARRAY);

    }

    /**
     * @return any exception associated with this node (may be null)
     */
    public Exception getException() {

        Exception e;

        if (exception != null) {
            e = exception;
        } else {
            e = getValue().getException();
        }

        return e;

    }

    /**
     * Determines whether the debug object contains a Hava exception.
     *
     * @return <tt>true</tt> if an exception is present,
     *         <tt>false</tt> otherwise
     */
    public boolean hasException() {

        return getException() != null;

    }

    /**
     * @return the name of the variable represented by this node
     */
    @Override
    public String toString() {

        if (exception != null) {
            return exception.getMessage();
        } else {
            return getValue().getName();
        }

    }

}
