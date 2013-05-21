package edu.gatech.hava.debug.trace;

import java.io.PrintWriter;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.parser.SimpleNode;

/**
 * An {@link HDebugListener} which represents the debug data
 * as a {@link Trace}.
 */
public class TraceDebugListener implements HDebugListener {

    private final Trace trace;

    private static class TFunction implements HDebugListener.Function {

        private final HReference ref;

        public TFunction(final HReference ref) {
            this.ref = ref;
        }

        public HReference getReference() {
            return ref;
        }

    }

    private static class TNode implements HDebugListener.Node {

        private final SimpleNode node;

        public TNode(final SimpleNode node) {
            this.node = node;
        }

        public SimpleNode getSimpleNode() {
            return node;
        }

    }

    /** {@inheritDoc} */
    public void start() {

    }

    /** {@inheritDoc} */
    public void finish() {

    }

    /**
     * Constructor.
     *
     * @param writer a writer which is given to the {@link Trace}
     *               to print its output
     */
    public TraceDebugListener(final PrintWriter writer) {

        trace = new Trace(writer);

    }

    /** {@inheritDoc} */
    public void addReference(final String identifier) {

    }

    /** {@inheritDoc} */
    public void errorEncountered(final HException x) {

    }

    /** {@inheritDoc} */
    public void evaluating(final HReference reference) {

        trace.log(reference,
                  Trace.SPACE,
                  Trace.ELLIPSIS);

        trace.push();

    }

    /** {@inheritDoc} */
    public Node evaluating(final SimpleNode n,
                           final String b) {

        if (n.getNumChildren() != 1) {

            trace.log(b,
                      Trace.SPACE,
                      Trace.ELLIPSIS);

            trace.push();

        }

        return new TNode(n);

    }

    /** {@inheritDoc} */
    public void ifEvaluationCompleted(final HValue value) {

        trace.pop();

        trace.log(Trace.IF_TRUE,
                  Trace.LBRACE,
                  value,
                  Trace.RBRACE);

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Node node,
                                    final String b,
                                    final HValue value) {

        final SimpleNode n = ((TNode) node).getSimpleNode();

        if (n.getNumChildren() == 1) {

            trace.log(b,
                      Trace.ASSIGN,
                      value);

        } else {

            trace.pop();

            trace.log(b,
                      Trace.ASSIGN,
                      value);

        }

    }

    /** {@inheritDoc} */
    public void evaluatingElse() {

        trace.log(Trace.ELSE,
                  Trace.ELLIPSIS);

        trace.push();

    }

    /** {@inheritDoc} */
    public void elseEvaluationCompleted(final HValue value) {

        trace.pop();

        trace.log(Trace.ELSE,
                  Trace.LBRACE,
                  value,
                  Trace.RBRACE);

    }

    /** {@inheritDoc} */
    public Function evaluatingFunction(final HReference reference) {

        trace.log(reference, Trace.SPACE, Trace.ELLIPSIS);
        trace.push();

        return new TFunction(reference);

    }

    /** {@inheritDoc} */
    public void evaluatingIf(final SimpleNode node,
                             final boolean conditionValue) {

        if (conditionValue) {

            trace.log(Trace.IF_TRUE,
                      Trace.ELLIPSIS);

            trace.push();

        } else {

            trace.log(Trace.IF_FALSE);

        }

    }

    /** {@inheritDoc} */
    public void evaluatingIteration(final String identifier,
                                    final HValue value) {

        trace.log(Trace.BULLET,
                  identifier,
                  Trace.ASSIGN,
                  value);

        trace.push();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final HReference reference,
                                    final HValue value) {

        trace.pop();

        trace.log(reference,
                  Trace.ASSIGN,
                  value);

    }

    /** {@inheritDoc} */
    public void evaluationCompleted() {

        trace.pop();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Function function,
                                    final HValue value) {

        trace.pop();

        trace.log(((TFunction) function).getReference(),
                  Trace.ASSIGN,
                  value);

    }

    /** {@inheritDoc} */
    public void addFilter(final HValue filterValue) {

        trace.log(Trace.LBRACE,
                  Trace.FILTER,
                  filterValue,
                  Trace.RBRACE);

    }

    /** {@inheritDoc} */
    public void addBracket(final HValue value) {

        trace.log(Trace.LBRACE,
                  value,
                  Trace.RBRACE);

    }

    /** {@inheritDoc} */
    public void addIn(final String identifierName,
                      final HValue value) {

        trace.log(identifierName,
                  Trace.IN,
                  value);


    }

    /** {@inheritDoc} */
    public void addAssignment(final String identifierName,
                              final HValue value) {

        trace.log(identifierName,
                  Trace.ASSIGN,
                  value);

    }

    /** {@inheritDoc} */
    public void addIterationRange(final String identifierName,
                                  final HValue v1,
                                  final HValue v2) {

        trace.log(identifierName,
                  Trace.ASSIGN,
                  v1,
                  Trace.TO,
                  v2);

    }

}
