package edu.gatech.hava.hdt.views.solution;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.gatech.hava.engine.HDocComment;
import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.hdt.views.filter.StringFilter;
import edu.gatech.hava.report.HReportGenerator;
import edu.gatech.hava.report.sort.HReferenceComparator;

/**
 * Encapsulates the results of a Hava script execution.
 *
 * Also takes care of sorting and filtering.
 */
class HavaSolution {

    private final HReportGenerator reportGenerator;
    private final Exception exception;
    private final HReference reference;

    private StringFilter<HReference> filter;

    private boolean sort = false;

    /**
     * Constructor.
     *
     * @param engine the engine, after it has been run
     * @param exception any exception thrown from the engine
     *                  (may be null)
     * @param reference the reference being evaluated as the
     *                  exception was thrown (may be null
     *                  only if <tt>exception</tt> is null)
     */
    HavaSolution(final HEngine engine,
                 final Exception exception,
                 final HReference reference) {

        this.reportGenerator = engine.getReportGenerator();
        this.exception = exception;
        this.reference = reference;

    }

    /**
     * Toggles sorting.
     */
    public void toggleSort() {

        sort = !sort;

    }

    void setFilter(final StringFilter<HReference> filter) {

        this.filter = filter;

    }

    private boolean include(final HReference ref) {

        final boolean include;

        if (sort && ref instanceof HDocComment) {
            include = false;
        } else {
            include = filter.select(ref);
        }

        return include;

    }

    /**
     * @return an array of references in the order in which
     *         they are to be displayed
     */
    HReference[] getReferences() {

        final List<HReference> refs = new ArrayList<HReference>();

        for (final HReference ref
                : reportGenerator.getDisplayedReferences()) {

            if (include(ref)) {
                refs.add(ref);
            }

        }

        if (sort) {
            Collections.sort(refs, new HReferenceComparator());
        }

        return refs.toArray(new HReference[0]);

    }

    /**
     * Retrieves a string which can be displayed for the
     * given reference.
     *
     * @param reference the reference
     * @return the string display for the given reference
     */
    String getDisplay(final HReference reference) {

        final String display = reportGenerator.get(reference);

        return display;

    }

    /**
     * @return true if an exception was thrown from the engine,
     *         false otherwise
     */
    boolean hasException() {

        return exception != null;

    }

    /**
     * @return the exception thrown from the engine
     *         (may be null if no exception was thrown)
     */
    Exception getException() {

        return exception;

    }

    /**
     * @return the reference being evaluated as an exception was
     *         thrown from the engine (may be null if no exception
     *         was thrown)
     */
    HReference getExceptionReference() {

        return reference;

    }

    /**
     * Writes the display string for a particular reference
     * to the given writer.
     *
     * @param writer the writer to which the display string
     *               is to be written
     * @param reference the reference whose display string
     *                  is to be written
     */
    void write(final StringWriter writer,
               final HReference reference) {

        reportGenerator.write(writer, reference);

    }

}
