package edu.gatech.hava.debug;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.engine.HValue;

/**
 * Abstract superclass for all of the objects which old
 * debug information.
 */
public abstract class HDebugObject {

    private List<HDebugObject> dependencies = new ArrayList<HDebugObject>();

    private HException exception;

    public abstract HValue getValue();

    public abstract String getName();

    public List<HDebugObject> getDependencies() {

        return dependencies;

    }

    public void addDependency(final HDebugObject obj) {

        if (!dependencies.contains(obj)) {
            dependencies.add(obj);
        }

    }

    public void setException(final HException e) {

        exception = e;

    }

    public HException getException() {

        return exception;

    }

    public boolean hasException() {

        return exception != null;

    }

}
