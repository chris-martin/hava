package edu.gatech.hava.lib.iterator;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.hava.engine.HIterator;
import edu.gatech.hava.engine.HValue;

class CollectInstance implements HIterator.HIteratorInstance {

    private List<HValue> list = new ArrayList<HValue>();

    public void addCase(final HValue i,
                        final HValue v) {

        list.add(v);

    }

    public boolean isComplete() {

        return false;

    }

    public HValue getValue() {

        HValue[] array = new HValue[list.size()];
        list.toArray(array);
        return new HValue(array);

    }

}
