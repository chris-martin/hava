
package edu.gatech.hava.report.simple;

import edu.gatech.hava.engine.HReference;

abstract class RowKey {

    private final HReference ref;

    RowKey(final HReference ref) {

        this.ref = ref;

    }

    HReference getReference() {

        return ref;

    }

}
