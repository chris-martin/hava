package edu.gatech.hava.report.table;

/**
 * Location of a header label.
 */
class LabelLocator {

    private int i;
    private int j;

    LabelLocator(final int i, final int j) {

        this.i = i;
        this.j = j;

    }

    public int hashCode() {

        return (i << 3) + j;

    }

    public boolean equals(final Object x) {

        if (!(x instanceof LabelLocator)) {
            return false;
        }
        LabelLocator ll = (LabelLocator) x;
        return i == ll.i && j == ll.j;

    }

}