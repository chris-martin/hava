package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.parser.SimpleNode;

/**
 * Represents an "if" or "else" block.
 */
public class HDebugConditional extends HDebugObject {

    private SimpleNode node;
    private String name;
    private HValue value;

    public HDebugConditional(final SimpleNode node,
                             final boolean value) {

        this.node = node;
        this.value = value ? HValue.TRUE : HValue.FALSE;

    }

    public HDebugConditional(final String name) {

        this.name = name;

    }

    /** {@inheritDoc} */
    public String getName() {

        if (name == null) {
            final String nodeString =
                (new SimpleNodeStringBuilder(node)).toString();
            name = "if (" + nodeString + ")";
        }

        return name;

    }

    /** {@inheritDoc} */
    @Override
    public HValue getValue() {

        return value;

    }

}
