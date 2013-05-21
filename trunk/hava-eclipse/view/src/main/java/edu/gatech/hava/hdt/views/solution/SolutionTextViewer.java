package edu.gatech.hava.hdt.views.solution;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * The text viewer which {@link HavaSolutionView} uses
 * to display the contents of a {@link HavaSolution}.
 */
public class SolutionTextViewer extends TextViewer {

    private static final int SWT_STYLE_BITS =
        SWT.H_SCROLL | SWT.V_SCROLL;

    /**
     * Create a new text viewer.
     *
     * @param parent the parent of the viewer's control
     */
    public SolutionTextViewer(final Composite parent) {

        super(parent, SWT_STYLE_BITS);

        getTextWidget().setTabs(9);

    }

    /**
     * Disable or enable alphanumeric sorting.
     */
    public void toggleSort() {

        getHavaSolution().toggleSort();

    }

    /**
     * Sets or clears the input for this viewer.
     *
     * @param input the input of this viewer (should be of type
     *              {@link HavaSolution}), or <code>null</code> if none
     */
    @Override
    public void setInput(final Object input) {

        if (input instanceof HavaSolution) {

            final HavaSolution solution = (HavaSolution) input;
            this.getHavaDocument().setSolution(solution);

        } else {

            super.setInput(input);

        }

    }

    /**
     * Refreshes this viewer completely with information
     * freshly obtained from this viewer's model.
     */
    @Override
    public void refresh() {

        this.getHavaDocument().refresh();
        super.refresh();

    }

    private HavaSolution getHavaSolution() {

        return getHavaDocument().getSolution();

    }

    private ReportDocument getHavaDocument() {

        return (ReportDocument) getDocument();

    }

}
