package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;

public class HDebugReference extends HDebugObject {

    private HReference reference;

    public HDebugReference(final HReference reference) {

        this.reference = reference;

    }

    public HReference getReference() {

        return reference;

    }

    public HValue getValue() {

        return reference.getValue();

    }

    public String getName() {

        return reference.toString();

    }

}
