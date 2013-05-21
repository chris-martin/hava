package edu.gatech.hava.report.simple;

import edu.gatech.hava.engine.HReference;

class RefRowKey extends RowKey {

    public RefRowKey(final HReference ref) {

        super(ref);

    }

    @Override
    public int hashCode() {

        return getReference().hashCode();

    }

    @Override
    public boolean equals(final Object obj) {

        return obj instanceof RefRowKey
            && ((RefRowKey) obj).getReference() == getReference();

    }

}
