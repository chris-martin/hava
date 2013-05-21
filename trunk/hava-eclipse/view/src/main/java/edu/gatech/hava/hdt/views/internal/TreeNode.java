package edu.gatech.hava.hdt.views.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic node in a tree data structure, which holds
 * references to the node's parent and children, as well
 * as the value represented by the node.
 *
 * The children are stored as a {@link List}, so their ordering
 * is retained.
 *
 * @param <T> the node's own type
 * @param <V> the type of object represented by the node
 */
public class TreeNode<T extends TreeNode<T, V>, V> {

    private final List<T> children = new ArrayList<T>();

    private final T parent;

    private final V value;

    /**
     * Empty array, to hack around Java generics, just for casting.
     */
    private final T[] array;

    /**
     * Constructs a new {@link TreeNode} with no parent and no children.
     *
     * @param value the value represented by this node
     * @param array an array of type {@link T}
     */
    public TreeNode(final V value, final T[] array) {

        this(null, value, array);

    }

    /**
     * Constructs a new {@link TreeNode} with the given parent and no children.
     *
     * @param parent this node's parent
     * @param value the value represented by this node
     * @param array an array of type {@link T}.
     */
    public TreeNode(final T parent, final V value, final T[] array) {

        this.parent = parent;
        this.value = value;
        this.array = array;

    }

    public V getValue() {

        return this.value;

    }

    /**
     * Adds a child to this node.
     *
     * The child is added to the end of the list of children.
     *
     * @param child the child node to add
     */
    public void addChild(final T child) {

        children.add(child);

    }

    /**
     * @return all of this node's children
     */
    public T[] getChildren() {

        return children.toArray(array);

    }

    /**
     * Determines whether this node has any children.
     *
     * @return <tt>true</tt> if this node has at least one child, or
     *         <tt>false</tt> if this node has no children
     */
    public boolean hasChildren() {

        return children.size() != 0;

    }

    /**
     * @return the last child of this node
     */
    public T getLastChild() {

        final T[] c = getChildren();

        if (c.length == 0) {
            return null;
        }

        return c[c.length - 1];

    }

    /**
     * @return this node's parent
     */
    public T getParent() {

        return parent;

    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return getValue().toString();

    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {

        boolean equals = false;

        if (obj instanceof TreeNode) {

            final TreeNode<?, ?> otherNode = (TreeNode<?, ?>) obj;

            final Object v1 = getValue();
            final Object v2 = otherNode.getValue();

            if (v1 == null) {
                equals = v2 == null;
            } else {
                equals = (v2 != null) && v1.equals(v2);
            }

        }

        return equals;

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {

        int hashCode = 0;

        final V val = getValue();

        if (val != null) {
            hashCode = val.hashCode();
        }

        return hashCode;

    }

}
