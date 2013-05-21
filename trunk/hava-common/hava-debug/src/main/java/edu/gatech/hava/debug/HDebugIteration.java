package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HValue;

/**
 * Represents a single iteration of a loop.
 */
public class HDebugIteration extends HDebugObject {

    private String name;
    private HValue value;

    /**
     * @param name the name of the variable being iterated
     *             over (for example, "i" if the iteration
     *             is "i = 1 to 20")
     * @param value the return value of the iteration
     */
    public HDebugIteration(final String name,
                           final HValue value) {

        this.name = name;
        this.value = value;

    }

    public HValue getValue() {

        return value;

    }

    public void setValue(final HValue value) {

        this.value = value;

    }

    public String getName() {

        return name;

    }

}
