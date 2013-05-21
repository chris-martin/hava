package edu.gatech.hava.lib.iterator;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HOperator;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.exception.IncompleteHavaException;

class ArgmaxInstance implements HIterator.HIteratorInstance {

    private HValue index = HValue.IGNORE;
    private HValue value = HValue.IGNORE;

    public void addCase(final HValue i,
                        final HValue v)
            throws IncompleteHavaException {

        if (!v.isNumerical()) {
            throw new IncompleteHavaException(HException.TYPE_MESSAGE);
        }

        if (value.equals(HValue.IGNORE)
                || HOperator.relationalGreaterThan(v, value).booleanValue()) {
            index = i;
            value = v;
        }

    }

    public boolean isComplete() {

        return false;

    }

    public HValue getValue() {

        return index;

    }

}
