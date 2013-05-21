package edu.gatech.hava.hdt.views.debug;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import edu.gatech.hava.debug.HDebugObject;
import edu.gatech.hava.debug.IDebugNodeProvider;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * A content provider which wraps {@link HDebugObject}s
 * in {@link DebugTreeNode}s.
 */
public class DebugTreeContentProvider implements ITreeContentProvider {

    private IDebugNodeProvider debugBase;
    private HDebugObject[] rootVariables = new HDebugObject[0];
    private DebugTreeNode[] rootNodes;

    /**
     * Returns the child elements of the given parent element.
     *
     * The result is not modified by the viewer.
     *
     * @param parentElement the parent element
     * @return an array of child elements
     */
    @Override
    public DebugTreeNode[] getChildren(final Object parentElement) {

        final DebugTreeNode parentNode = (DebugTreeNode) parentElement;
        final HDebugObject parentObject = parentNode.getValue();

        if (!parentNode.hasChildren()) {
            if (parentObject.hasException()) {
                parentNode.addChild(new DebugTreeNode(parentNode, parentObject.getException()));
            }
            for (HDebugObject debugVar : parentObject.getDependencies()) {
                parentNode.addChild(new DebugTreeNode(parentNode, debugVar));
            }
        }

        return parentNode.getChildren();

    }

    /**
     * Returns the parent for the given element, or <code>null</code>
     * indicating that the parent can't be computed.
     *
     * @param element the element
     * @return the parent element, or <code>null</code> if it
     *         has none or if the parent cannot be computed
     */
    @Override
    public Object getParent(final Object element) {

        DebugTreeNode parentNode = (DebugTreeNode) element;
        return parentNode.getParent();

    }

    /**
     * Returns whether the given element has children.
     *
     * Intended as an optimization for when the viewer does not
     * need the actual children.
     *
     * @param element the element
     * @return <code>true</code> if the given element has children,
     *         and <code>false</code> if it has no children
     */
    @Override
    public boolean hasChildren(final Object element) {

        final DebugTreeNode node = (DebugTreeNode) element;
        final HDebugObject debugObject = node.getValue();

        if (debugObject == null) {
            return false;
        }

        final List<HDebugObject> dependencies = debugObject.getDependencies();
        final boolean hasChildren = dependencies.size() > 0 || debugObject.hasException();

        return hasChildren;

    }

    /**
     * Returns the root elements to display in the viewer
     * when its input is set to the given element.
     *
     * The result is not modified by the viewer.
     *
     * @param inputElement the input element (must be of type
     *                     {@link IDebugNodeProvider}
     *                     or {@link HException}).
     * @return the array of elements to display in the viewer
     */
    @Override
    public DebugTreeNode[] getElements(final Object inputElement) {

        if (inputElement == null) {

            rootNodes = new DebugTreeNode[0];

        } else if (inputElement instanceof Exception) {

            final Exception inputException = (Exception) inputElement;

            debugBase = null;
            rootNodes = new DebugTreeNode[] {
                new DebugTreeNode(inputException)
            };

        } else if (inputElement instanceof IDebugNodeProvider) {

            final IDebugNodeProvider inputDebugBase = (IDebugNodeProvider) inputElement;

            /* Only re-create the root nodes if the inputElement has
             * changed since the last time getElements was called. */
            if (inputDebugBase != debugBase) {

                debugBase = inputDebugBase;

                final List<HDebugObject> debugObjects = debugBase.getTopLevelVariables();
                rootVariables = debugObjects.toArray(new HDebugObject[0]);

                rootNodes = new DebugTreeNode[rootVariables.length];

                for (int i = 0; i < rootNodes.length; i++) {
                    final HDebugObject debugVar = rootVariables[i];
                    rootNodes[i] = new DebugTreeNode(debugVar);
                }

            }

        } else {

            HavaViewPlugin.getDefault().log(IStatus.ERROR,
                    "Illegal inputElement type " + inputElement.getClass().toString(),
                    new ClassCastException());

            rootNodes = new DebugTreeNode[0];

        }

        return rootNodes;

    }

    /**
     * Disposes of this content provider.
     * This is called by the viewer when it is disposed.
     */
    @Override
    public void dispose() {

    }

    /** {@inheritDoc} */
    @Override
    public void inputChanged(final Viewer viewer,
                             final Object oldInput,
                             final Object newInput) {

    }

}
