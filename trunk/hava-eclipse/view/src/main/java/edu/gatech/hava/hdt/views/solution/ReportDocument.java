package edu.gatech.hava.hdt.views.solution;

import java.io.File;
import java.io.StringWriter;

import org.eclipse.jface.text.Document;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;

/**
 * Document which shows the Hava report,
 * based on a {@link HavaSolution}.
 */
public class ReportDocument extends Document {

    private HavaSolution solution;

    /**
     * Retrieves the {@link HavaSolution} which provides the
     * text for this document.
     *
     * @return a {@link HavaSolution} (may be null)
     */
    public HavaSolution getSolution() {

        return solution;

    }

    /**
     * Sets or clears the {@link HavaSolution} which provides the
     * text for this document.
     *
     * @param solution a {@link HavaSolution}, or null.
     */
    public void setSolution(final HavaSolution solution) {

        this.solution = solution;

        updateSolutionText();

    }

    /**
     * Updates the text based on this document's {@link HavaSolution}.
     */
    public void refresh() {

        updateSolutionText();

    }

    private void updateSolutionText() {

        final String text;

        if (solution == null) {
            text = "";
        } else {
            final HReference[] references = solution.getReferences();
            text = getText(references);
        }

        this.set(text);

    }

    private String getText(final HReference[] references) {

        final StringWriter text = new StringWriter();

        for (final HReference reference : references) {
            solution.write(text, reference);
        }

        if (solution.hasException()) {

            final Exception e = solution.getException();

            final String errorMessage;

            if (e instanceof HException) {

                final HException he = (HException) e;

                if (he.getFile() == null) {

                    errorMessage = String.format(
                            "Error at line %d, col %d: %s",
                            he.getLine(),
                            he.getColumn(),
                            he.getMessage());

                } else {

                    final File errorFile = new File(he.getFile());

                    errorMessage = String.format(
                            "Error in file %s at line %d, col %d: %s",
                            errorFile.getName(),
                            he.getLine(),
                            he.getColumn(),
                            he.getMessage());

                }

            } else {

                errorMessage = e.getMessage();

            }

            text.write(errorMessage);
            text.write('\n');

        }

        return text.toString();

    }

}
