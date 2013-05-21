package edu.gatech.hava.hdt.views.jump;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import edu.gatech.hava.hdt.views.internal.HavaViewPlugin;

/**
 * A {@link JumpAction} which is associated with a
 * {@link StructuredViewer}.
 *
 * The location to which this action jumps changed based on the
 * structured viewer's selection.
 *
 * @param <T> the type of value that the viewer's selection holds
 */
public abstract class StructuredViewerJumpAction<T> extends JumpAction {

    private final StructuredViewer viewer;

    /**
     * Constructor.
     *
     * @param viewer the structured viewer to which this action belongs.
     */
    public StructuredViewerJumpAction(final StructuredViewer viewer) {

        this.viewer = viewer;

    }

    /**
     * @param selectedElement the first element which is currently
     *                        selected (may be null)
     * @return a Hava reference which will be used to determine
     *         which code will be jumped to
     */
    protected abstract JumpLocation getReference(T selectedElement);

    /** {@inheritDoc} */
    @Override
    public JumpLocation getReference() {

        final IStructuredSelection selection = getSelection();
        final Object selectedElement = selection.getFirstElement();
        final T t = castSelectedElement(selectedElement);

        return getReference(t);

    }

    @SuppressWarnings("unchecked")
    private T castSelectedElement(final Object selectedElement) {

        T t = null;

        try {

            t = (T) selectedElement;

        } catch (final ClassCastException e) {

            final String message = "Illegal selectedElement type "
                + selectedElement.getClass().toString();

            HavaViewPlugin.getDefault().log(IStatus.ERROR, message, e);

        }

        return t;

    }

    private IStructuredSelection getSelection() {

        return (IStructuredSelection) viewer.getSelection();

    }

}
