package edu.gatech.hava.debug;

import edu.gatech.hava.engine.HValue;
import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.parser.SimpleNode;

public class HDebugSimpleNode extends HDebugObject
        implements HDebugListener.Node {

    private SimpleNode node;
    private HValue value;
    private String name;

    public HDebugSimpleNode(final SimpleNode node) {

        this.node = node;

    }

    public void setValue(final HValue value) {

        this.value = value;

    }

    @Override
    public String getName() {

        if (null == name) {
            name = (new SimpleNodeStringBuilder(node)).toString();
        }

        return name;

    }

    @Override
    public HValue getValue() {

        return value;

    }

}
