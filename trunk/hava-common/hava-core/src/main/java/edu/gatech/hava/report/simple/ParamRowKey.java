package edu.gatech.hava.report.simple;

import edu.gatech.hava.engine.HParameters;
import edu.gatech.hava.engine.HReference;

class ParamRowKey extends RowKey {

    public ParamRowKey(final HReference ref) {

        super(ref);

    }

    private HParameters getParameters() {

        return getReference().getParameters();

    }

    @Override
    public int hashCode() {

        final int hash;

        final HParameters params = getParameters();

        if (params == null) {
            hash = 0;
        } else {
            hash = params.hashCode();
        }

        return hash;

    }



    @Override
    public boolean equals(final Object obj) {

        return obj instanceof ParamRowKey
            && ((ParamRowKey) obj).getParameters() == getParameters();

    }

}
