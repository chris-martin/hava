package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HValue;

public class HDebugString extends HDebugObject {

    private String name;

    public HDebugString(final String name) {

        this.name = name;

    }

    /** {@inheritDoc} */
    public String getName() {

        return name;

    }

    /** {@inheritDoc} */
    @Override
    public HValue getValue() {

        return null;

    }

}
