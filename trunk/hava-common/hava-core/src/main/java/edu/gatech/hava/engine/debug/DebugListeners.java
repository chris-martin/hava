package edu.gatech.hava.engine.debug;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.parser.SimpleNode;

public class DebugListeners implements HDebugListener {

    private final Set<HDebugListener> listeners =
        new HashSet<HDebugListener>();

    private class NodeMap extends HashMap<HDebugListener, Node>
            implements Node { }

    private class FunctionMap extends HashMap<HDebugListener, Function>
            implements Function { }

    public void start() {

        for (final HDebugListener listener : listeners) {
            listener.start();
        }

    }

    public void finish() {

        for (final HDebugListener listener : listeners) {
            listener.finish();
        }

    }

    public void add(final HDebugListener listener) {

        listeners.add(listener);

    }

    public void remove(final HDebugListener listener) {

        listeners.remove(listener);

    }

    public void errorEncountered(final HException x) {

        for (final HDebugListener listener : listeners) {
            listener.errorEncountered(x);
        }

    }

    public void evaluating(final HReference reference) {

        for (final HDebugListener listener : listeners) {
            listener.evaluating(reference);
        }

    }

    public Node evaluating(final SimpleNode node,
                           final String b) {

        final NodeMap nodeMap = new NodeMap();

        for (final HDebugListener listener : listeners) {
            nodeMap.put(listener, listener.evaluating(node, b));
        }

        return nodeMap;

    }

    public Function evaluatingFunction(final HReference reference) {

        final FunctionMap functionMap = new FunctionMap();

        for (final HDebugListener listener : listeners) {
            functionMap.put(listener, listener.evaluatingFunction(reference));
        }

        return functionMap;

    }

    public void evaluatingIteration(final String identifier,
                                    final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.evaluatingIteration(identifier, value);
        }

    }

    public void evaluatingIf(final SimpleNode node,
                             final boolean conditionValue) {

        for (final HDebugListener listener : listeners) {
            listener.evaluatingIf(node, conditionValue);
        }

    }

    public void evaluatingElse() {

        for (final HDebugListener listener : listeners) {
            listener.evaluatingElse();
        }

    }

    public void evaluationCompleted() {

        for (final HDebugListener listener : listeners) {
            listener.evaluationCompleted();
        }

    }

    public void evaluationCompleted(final HReference reference,
                                    final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.evaluationCompleted(reference, value);
        }

    }

    public void evaluationCompleted(final Node node,
                                    final String b,
                                    final HValue value) {

        final NodeMap nodeMap = (NodeMap) node;

        for (final HDebugListener listener : listeners) {
            listener.evaluationCompleted(nodeMap.get(listener), b, value);
        }

    }

    public void evaluationCompleted(final Function function,
                                    final HValue value) {

        final FunctionMap functionMap = (FunctionMap) function;

        for (final HDebugListener listener : listeners) {
            listener.evaluationCompleted(functionMap.get(listener), value);
        }

    }

    public void addAssignment(final String identifierName,
                              final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.addAssignment(identifierName, value);
        }

    }

    public void addBracket(final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.addBracket(value);
        }

    }

    public void addFilter(final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.addFilter(value);
        }

    }

    public void addIn(final String identifierName,
                      final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.addIn(identifierName, value);
        }

    }

    public void addIterationRange(final String identifierName,
                                  final HValue v1,
                                  final HValue v2) {

        for (final HDebugListener listener : listeners) {
            listener.addIterationRange(identifierName, v1, v2);
        }

    }

    public void elseEvaluationCompleted(final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.elseEvaluationCompleted(value);
        }

    }

    public void ifEvaluationCompleted(final HValue value) {

        for (final HDebugListener listener : listeners) {
            listener.ifEvaluationCompleted(value);
        }

    }

}
