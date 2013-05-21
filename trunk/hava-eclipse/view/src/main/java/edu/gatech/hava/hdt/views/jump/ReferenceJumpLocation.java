package edu.gatech.hava.hdt.views.jump;

import edu.gatech.hava.engine.HReference;

/**
 * A {@link JumpLocation} specified by an {@link HReference}.
 */
public class ReferenceJumpLocation implements JumpLocation {

    private final HReference reference;

    /**
     * Constructor.
     *
     * @param reference a reference whose declaration
     *                  is the jump location
     */
    public ReferenceJumpLocation(final HReference reference) {

        this.reference = reference;

    }

    @Override
    public int getLine() {

        return reference.getReferenceToken().getBeginLine();

    }

    @Override
    public String getFile() {

        return reference.getAddress();

    }

}
