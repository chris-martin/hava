package edu.gatech.hava.report.table;

import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;

/**
 * A key identifies a value bundled into a reference.
 * It is implemented as an array of integers.
 * The first integer is i>=0 for i-th index, or -1 for value.
 * Subsequent integers are list indexes or structure field numbers.
 *
 * Consider, for example, an indexed variable x(i) whose value
 * is a structure S(a, b, c).  The key [0] identifies the index i.
 * The key [-1, 2] identifies the field x(i).b.
 *
 * Keys can also accomodate more complex situations,
 * such as an index whose value is a structure,
 * and nested structures in the reference's index or value.
 */
class Key {

    private int[] a;

    // Key to index i (root)
    Key(final int i) {

        a = new int[] {i};

    }

    // Key to value (root)
    Key() {

        a = new int[] {-1};

    }

    // Extend key by one level
    Key(final Key k, final int i) {

        int n = k.depth();
        a = new int[n + 1];
        System.arraycopy(k.a, 0, a, 0, n);
        a[n] = i;

    }

    int depth() {

        return a.length;

    }

    HValue extractFromReference(final HReference x) {

        HValue v;
        if (a[0] < 0) {
            v = x.getValue();
        } else {
            v = x.getElement(a[0]);
        }

        return extractFromValue(v);

    }

    HValue extractFromValue(HValue v) {

        for (int j = 1; j < depth(); j++) {
            if (v.equals(HValue.BLANK)) {
                return null;
            }
            if (a[j] >= v.getNumElements()) {
                return null;
            }
            v = v.getElement(a[j]);
            if (v == null) {
                return null;
            }
        }
        return v;

    }

    String getLabelFromReference(final HReference x) {

        int n = depth();

        if (n == 1) {
            if (a[0] >= 0) {
                return x.getParameters().getFieldIdentifier(a[0]);
            } else {
                return "value";
            }
        }

        HValue v;
        if (a[0] < 0) {
            v = x.getValue();
        } else {
            v = x.getElement(a[0]);
        }

        return getLabelFromValue(v);

    }

    String getLabelFromValue(HValue v) {

        int n = depth();
        for (int j = 1; j < n - 1; j++) {
            v = v.getElement(a[j]);
        }

        if (v.isStructure()) {
            return v.getParameters().getFieldIdentifier(a[n - 1]);
        } else {
            return null;
        }

    }

}
