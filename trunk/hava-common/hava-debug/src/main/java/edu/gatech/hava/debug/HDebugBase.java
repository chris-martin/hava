package edu.gatech.hava.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.parser.SimpleNode;

/**
 * An {@link HDebugListener} implementation which logs all of the debug
 * information, and provides it as a list of {@link HDebugObject}s.
 */
public class HDebugBase implements HDebugListener, IDebugNodeProvider {

    private Stack<HDebugObject> variableStack = new Stack<HDebugObject>();

    private Stack<HDebugReference> referenceStack = new Stack<HDebugReference>();

    private HDebugReference errorVariable;

    /**
     * A dictionary from names to debug objects, so that if two parts of
     * the debug tree refer to the same thing, they can share the same node.
     */
    private Map<String, HDebugObject> uniqueVariableMap = new HashMap<String, HDebugObject>();

    private boolean running = false;

    private HException exception;

    /** {@inheritDoc} */
    public void start() {

        variableStack.clear();
        uniqueVariableMap.clear();
        running = true;

    }

    /** {@inheritDoc} */
    public void finish() {

        running = false;

    }

    /** {@inheritDoc} */
    public void errorEncountered(final HException x) {

        exception = x;

        if (!variableStack.empty()) {
            HDebugObject errorObject = variableStack.pop();
            errorObject.setException(x);
        }

        errorVariable = referenceStack.peek();

        variableStack.clear();

    }

    /** {@inheritDoc} */
    public void evaluating(final HReference reference) {

        if (running) {
            HDebugReference newVariable = new HDebugReference(reference);
            addNewVariable(newVariable);
            uniqueVariableMap.put(newVariable.getName(), newVariable);
            referenceStack.push(newVariable);
        }

    }

    /** {@inheritDoc} */
    public Function evaluatingFunction(final HReference reference) {

        HDebugFunction newVariable = new HDebugFunction(reference);

        if (running) {
            addNewVariable(newVariable);
        }

        return newVariable;

    }

    /** {@inheritDoc} */
    public Node evaluating(final SimpleNode node,
                           final String b) {

        if (running) {
            HDebugSimpleNode newVariable = new HDebugSimpleNode(node);
            addNewVariable(newVariable);
            return newVariable;
        }

        return null;

    }

    /** {@inheritDoc} */
    public void evaluatingNonUnique(final String identifier) {

        if (running) {
            addNewVariable(new HDebugString(identifier));
        }

    }

    /** {@inheritDoc} */
    public void evaluatingIteration(final String identifier,
                                    final HValue value) {

        if (running) {
            addNewVariable(new HDebugIteration(identifier, value));
        }

    }

    /** {@inheritDoc} */
    public void evaluatingIf(final SimpleNode node,
                             final boolean conditionValue) {

        if (running) {

            if (conditionValue) {
                addNewVariable(new HDebugConditional(node, true));
            } else {
                addNewVariable(new HDebugConditional(node, false));
                evaluationCompleted();
            }

        }

    }

    /** {@inheritDoc} */
    public void evaluatingElse() {

        if (running) {

            addNewVariable(new HDebugConditional("else "));

        }

    }

    private void addNewVariable(final HDebugObject newVariable) {

        // Add dependency
        if (variableStack.size() > 0) {
            variableStack.peek().addDependency(newVariable);
        }

        variableStack.push(newVariable);

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Function function,
                                    final HValue value) {

        ((HDebugFunction) function).setValue(value);

        evaluationCompleted();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final Node node,
                                    final String b,
                                    final HValue value) {

        ((HDebugSimpleNode) node).setValue(value);

        evaluationCompleted();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted(final HReference reference,
                                    final HValue value) {

        referenceStack.pop();

        evaluationCompleted();

    }

    /** {@inheritDoc} */
    public void evaluationCompleted() {

        if (running) {
            variableStack.pop();
        }

    }

    private void addReference(final String identifier) {

        if (running) {

            final HDebugObject referencedVariable;
            if (uniqueVariableMap.containsKey(identifier)) {
                referencedVariable = uniqueVariableMap.get(identifier);
            } else {
                referencedVariable = new HDebugString(identifier);
            }

            if (variableStack.size() != 0) {
                variableStack.peek().addDependency(referencedVariable);
            }

        }

    }

    /** {@inheritDoc} */
    public void addFilter(final HValue filterValue) {

        addReference("{FILTER " + filterValue + "}");

    }

    /** {@inheritDoc} */
    public void addBracket(final HValue value) {

        addReference("{" + value + "}");

    }

    /** {@inheritDoc} */
    public void addIn(final String identifierName,
                      final HValue value) {

        addReference(identifierName + " in " + value);

    }

    /** {@inheritDoc} */
    public void addAssignment(final String identifierName,
                              final HValue value) {

        addReference(identifierName + " = " + value);

    }

    /** {@inheritDoc} */
    public void addIterationRange(final String identifierName,
                                  final HValue v1,
                                  final HValue v2) {

        addReference(identifierName + " = "
                + v1.toString() + " to " + v2.toString());

    }

    /** {@inheritDoc} */
    public List<HDebugObject> getTopLevelVariables() {

        return new ArrayList<HDebugObject>(uniqueVariableMap.values());

    }

    /** {@inheritDoc} */
    public HException getException() {

        return exception;

    }

    /** {@inheritDoc} */
    public boolean hasError() {

        return getException() != null;

    }

    /** {@inheritDoc} */
    public void elseEvaluationCompleted(final HValue value) {

        evaluationCompleted();

    }

    /** {@inheritDoc} */
    public void ifEvaluationCompleted(final HValue value) {

        evaluationCompleted();

    }

    public HDebugReference getErrorVariable() {

        return errorVariable;

    }

}
