package edu.gatech.hava.report.sort;

import java.util.Comparator;

import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;

/**
 * Recursive comparator to sort values in the index.
 */
public class HIndexComparator implements Comparator<HReference> {

    /** {@inheritDoc} */
    public int compare(final HReference a, final HReference b) {

        // First, sort on definition rank (presumably within same import)
        int d = a.compareDefinitionRank(b);
        if (d != 0) {
            return d;
        }

        // Then, sort on the index value.
        return compareValues(a, b);

    }

    private int compareValues(final HValue a, final HValue b) {

        // If both numbers, sort by value.
        // Note that numbers may be of two different types, INTEGER or REAL.
        if (a.isNumerical() && b.isNumerical()) {
            return compareNumericalValues(a, b);
        }

        // Sort by type. For example, numbers go ahead of structures and lists.
        int iDiff = a.getType().compareDefinitionRank(b.getType());
        if (iDiff != 0) {
            return iDiff;
        }

        // If compound values, sort on size, then first element, etc.
        if (a.isCompound()) {
            return compareCompoundValues(a, b);
        }

        // If boolean, true then false.
        if (a.isBoolean()) {
            return (a.booleanValue() ? 0 : 1) - (b.booleanValue() ? 0 : 1);
        }

        // If a token, sort by order in which it was defined.
        if (a.isToken()) {
            return a.compareDefinitionRank(b);
        }

        // Shouldn't get here ...
        return 0;

    }

    private int compareNumericalValues(final HValue a,
                                       final HValue b) {

        double dDiff = a.doubleValue() - b.doubleValue();

        if (dDiff < 0) {
            return -1;
        }

        if (dDiff > 0) {
            return 1;
        }

        return 0;

    }

    private int compareCompoundValues(final HValue a,
                                      final HValue b) {

        int iDiff = a.getNumElements() - b.getNumElements();

        if (iDiff != 0) {
            return iDiff;
        }

        for (int i = 0; i < a.getNumElements(); i++) {
            iDiff = compareValues(a.getElement(i), b.getElement(i));
            if (iDiff != 0) {
                return iDiff;
            }
        }

        return 0;

    }

}
