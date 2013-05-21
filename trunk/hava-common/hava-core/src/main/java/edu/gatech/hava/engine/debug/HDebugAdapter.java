package edu.gatech.hava.engine.debug;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.parser.SimpleNode;

/**
 * An abstract {@link HDebugListener} implementation which does nothing.
 *
 * For the convenience of subclasses which don't care what events they
 * get, this class also called {@link #debugEvent()} from every method.
 */
public class HDebugAdapter implements HDebugListener {

    /**
     * This method is called when any of the events are fired.
     */
    protected void debugEvent() { }

    /** {@inheritDoc} */
    public void addReference(final String identifier) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void errorEncountered(final HException x) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluating(final HReference reference) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public Node evaluating(final SimpleNode node) {

        debugEvent();

        return null;

    }

    /** {@inheritDoc} */
    public Function evaluatingFunction(final HReference reference) {

        debugEvent();

        return null;

    }

    /** {@inheritDoc} */
    public void evaluatingNonUnique(final String identifier) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluatingIteration(final String identifier,
                                    final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluatingIf(final SimpleNode node,
                             final boolean conditionValue) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluatingElse() {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Node node,
                                    final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted() {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Function function,
                                    final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final HReference reference,
                                    final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void addAssignment(final String identifierName,
                              final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void addBracket(final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void addFilter(final HValue filterValue) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void addIn(final String identifierName,
                      final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void addIterationRange(final String identifierName,
                                  final HValue v1,
                                  final HValue v2) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void elseEvaluationCompleted(final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public Node evaluating(final SimpleNode node,
                           final String b) {

        debugEvent();

        return null;

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Node node,
                                    final String b,
                                    final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void ifEvaluationCompleted(final HValue value) {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void finish() {

        debugEvent();

    }

    /** {@inheritDoc} */
    public void start() {

        debugEvent();

    }

}
