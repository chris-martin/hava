package edu.gatech.hava.report.sort;

import java.util.Comparator;

import edu.gatech.hava.engine.HReference;

public class HReferenceComparator
        implements Comparator<HReference> {

    public int compare(final HReference n1, HReference n2) {

        return str(n1).compareToIgnoreCase(str(n2));

    }

    final String str(final HReference ref) {

        final String str;

        if (ref != null) {
            str = ref.toString();
        } else {
            str = "";
        }

        return str;

    }

}
