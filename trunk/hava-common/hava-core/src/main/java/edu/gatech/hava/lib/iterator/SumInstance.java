package edu.gatech.hava.lib.iterator;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HOperator;
import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.exception.IncompleteHavaException;

class SumInstance implements HIterator.HIteratorInstance {

    private HValue value = HValue.IGNORE;

    public void addCase(final HValue i, HValue v)
            throws IncompleteHavaException {

        if (v.isList()) {
            for (int j = 0; j < v.getNumElements(); j++) {
                if (!isSummable(v.getElement(j))) {
                    throw new IncompleteHavaException(HException.TYPE_MESSAGE);
                }
            }
        } else if (!isSummable(v)) {
            throw new IncompleteHavaException(HException.TYPE_MESSAGE);
        }

        if (value.equals(HValue.IGNORE)) {

            if (v.isBoolean()) {
                v = sumFix(v);
            }

            if (v.isList()) {
                int n = v.getNumElements();
                HValue[] a = new HValue[n];
                for (int k = 0; k < n; k++) {
                    a[k] = sumFix(v.getElement(k));
                }
                v = new HValue(a);
            }

            value = v;
            return;
        }

        if (isSummable(value) && isSummable(v)) {
            value = sumAdd(value, v);
            return;
        }

        if (value.isList() && v.isList() && value.getNumElements() == v.getNumElements()) {
            int n = value.getNumElements();
            HValue[] a = new HValue[n];
            for (int j = 0; j < n; j++) {
                a[j] = sumAdd(value.getElement(j), v.getElement(j));
            }
            value = new HValue(a);
            return;
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    private static boolean isSummable(final HValue a) {

        return a.isNumerical() || a.isBoolean();

    }

    private static HValue sumAdd(final HValue a, HValue b)
            throws IncompleteHavaException {

        if (b.isBoolean()) {
            b = sumFix(b);
        }

        return HOperator.add(a, b);

    }

    private static HValue sumFix(final HValue v) {

        if (v.isBoolean()) {
            if (v.booleanValue()) {
                return HValue.ONE;
            } else {
                return HValue.ZERO;
            }
        } else {
            return v;
        }

    }

    public boolean isComplete() {

        return false;

    }

    public HValue getValue() {

        if (value.equals(HValue.IGNORE)) {
            value = HValue.ZERO;
        }

        return value;

    }

}
