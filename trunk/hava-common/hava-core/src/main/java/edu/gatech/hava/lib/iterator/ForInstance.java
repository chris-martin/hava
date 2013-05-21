package edu.gatech.hava.lib.iterator;

import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HValue;

class ForInstance implements HIterator.HIteratorInstance {

    private HValue value;

    public void addCase(final HValue i,
                        final HValue v) {

        if (v.equals(HValue.IGNORE)) {
            return;
        }

        if (value != null) {
            value = HValue.ERROR;
            return;
        }

        value = v;

    }

    public boolean isComplete() {

        return false;

    }

    public HValue getValue() {

        if (value == null) {
            value = HValue.ERROR;
        }
        return value;

    }

}
