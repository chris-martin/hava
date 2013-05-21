package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.debug.HDebugListener;

/**
 * Represents a function call.
 */
public class HDebugFunction extends HDebugReference
        implements HDebugListener.Function {

    private HValue value = HValue.BLANK;

    public HDebugFunction(final HReference reference) {

        super(reference);

    }

    public void setValue(final HValue value) {

        this.value = value;

    }

    /** {@inheritDoc} */
    @Override
    public HValue getValue() {

        return value;

    }

}
