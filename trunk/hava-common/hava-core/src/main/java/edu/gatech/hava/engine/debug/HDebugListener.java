package edu.gatech.hava.engine.debug;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.parser.SimpleNode;

/**
 * As the HEngine executes, it fires events to provide information
 * to debugging tools.  A class which listens on debug events much
 * implement the HDebugListener interface, and an instance must be
 * hooked up to the engine by calling
 * {@link edu.gatech.hava.engine.HEngine#addDebugListener(HDebugListener)
 * to begin receiving events.
 *
 * Most of these functions are void, but some return objects
 * of type {@link HDebugListener.Function} or {@link HDebugListener.Node}.
 * These interfaces have no methods, and the engine does not use these
 * objects for anything, but it is required to provide them as arguments
 * to subsequent method invocations.
 * See {@link #evaluatingFunction(HReference)} and
 * {@link #evaluating(SimpleNode, String) for more explanation.
 */
public interface HDebugListener {

    public interface Function { }

    public interface Node { }

    /**
     * Called exactly once to indicate that execution has started.
     */
    void start();

    /**
     * Called exactly once to indicate that execution has halted.
     * This may be because the script has completed successfully,
     * or because it has stopped due to an error.
     */
    void finish();

    /**
     * Called when an {@link HException} is thrown from the engine.
     *
     * @param exception the exception which was thrown
     */
    void errorEncountered(HException exception);

    /**
     * Called when beginning to execute a function.
     *
     * @return an object which must be provided as an argument to
     *         {@link #evaluationCompleted(Function, HValue)
     *         after the function has been evaluated
     */
    Function evaluatingFunction(HReference reference);

    /**
     * Called when a function call returns.
     *
     * @param function the object returned from
     *                 {@link #evaluatingFunction(HReference)}
     *                 when the function evaluation began
     * @param value the return value of the evaluated function
     */
    void evaluationCompleted(Function function,
                             HValue value);

    Node evaluating(SimpleNode node,
                    String b);

    void evaluationCompleted(Node node,
                             String b,
                             HValue value);

    void evaluating(HReference reference);

    /**
     * Called when an iteration in a loop is evaluated.
     *
     * @param identifier the name of the variable being iterated
     *                   over (for example, "i" if the iteration
     *                   is "i = 1 to 20")
     * @param value the return value of the iteration
     */
    void evaluatingIteration(String identifier,
                             HValue value);

    /**
     * Indicates that an "if" statement is being evaluated.
     *
     * If the "if" condition is true, this means that the conditional block
     * will be executed, and evaluationCompleted() should be called upon its
     * completion. If false, evaluationCompleted() should not be called.
     *
     * @param conditionValue Whether the "if" condition is true or false.
     */
    void evaluatingIf(SimpleNode node,
                      boolean conditionValue);

    /**
     * Indicated than an "else" statement is being evaluated.
     *
     * evaluationCompleted() should be called upon its completion.
     */
    void evaluatingElse();

    void evaluationCompleted(HReference reference,
                             HValue value);

    void evaluationCompleted();

    /**
     * Called after the body of an "if" block has been evaluated.
     *
     * @param value the return value of the code block
     */
    void ifEvaluationCompleted(HValue value);

    /**
     * Called after the body of an "else" block has been evaluated.
     *
     * @param value the return value of the code block
     */
    void elseEvaluationCompleted(HValue value);

    void addFilter(HValue filterValue);

    void addBracket(HValue value);

    void addIn(String identifierName,
               HValue value);

    void addAssignment(String identifierName,
                       HValue value);

    void addIterationRange(String identifierName,
                           HValue v1,
                           HValue v2);

}
