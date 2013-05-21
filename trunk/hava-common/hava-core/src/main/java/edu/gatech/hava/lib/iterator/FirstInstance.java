package edu.gatech.hava.lib.iterator;

import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HValue;

class FirstInstance implements HIterator.HIteratorInstance {

    private HValue value = HValue.IGNORE;

    public void addCase(final HValue i,
                        final HValue v) {

        value = v;

    }

    public boolean isComplete() {

        return !value.equals(HValue.IGNORE);

    }

    public HValue getValue() {

        return value;

    }

}
