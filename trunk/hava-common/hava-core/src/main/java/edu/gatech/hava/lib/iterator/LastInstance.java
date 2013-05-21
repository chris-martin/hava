package edu.gatech.hava.lib.iterator;

import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HValue;

class LastInstance implements HIterator.HIteratorInstance {

    private HValue value = HValue.IGNORE;

    public void addCase(final HValue i, final HValue v) {

        if (!v.equals(HValue.IGNORE)) {
            value = v;
        }

    }

    public boolean isComplete() {

        return false;

    }

    public HValue getValue() {

        return value;

    }

}
