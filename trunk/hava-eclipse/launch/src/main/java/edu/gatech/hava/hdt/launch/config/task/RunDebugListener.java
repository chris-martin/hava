package edu.gatech.hava.hdt.launch.config.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import edu.gatech.hava.debug.HDebugObject;
import edu.gatech.hava.debug.HDebugReference;
import edu.gatech.hava.debug.IDebugNodeProvider;
import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.debug.HDebugAdapter;

class RunDebugListener extends HDebugAdapter
        implements IDebugNodeProvider {

    private Stack<HReference> stack = new Stack<HReference>();

    private HDebugReference errorVariable;

    private List<HDebugObject> references = new ArrayList<HDebugObject>();

    private HException exception;

    /** {@inheritDoc} */
    @Override
    public void evaluating(final HReference reference) {

        stack.push(reference);

    }

    /** {@inheritDoc} */
    @Override
    public void evaluationCompleted(final HReference reference,
                                    final HValue value) {

        stack.pop();

        final HDebugReference newVariable =
            new HDebugReference(reference);
        references.add(newVariable);

    }

    /** {@inheritDoc} */
    @Override
    public void errorEncountered(final HException exception) {

        this.exception = exception;

        errorVariable =
            new HDebugReference(stack.peek());
        references.add(errorVariable);
        errorVariable.setException(exception);

    }

    @Override
    public HException getException() {

        return exception;

    }

    public boolean hasError() {

        return exception != null;

    }

    @Override
    public List<HDebugObject> getTopLevelVariables() {

        return references;

    }

    @Override
    public HDebugReference getErrorVariable() {

        return errorVariable;

    }

}
