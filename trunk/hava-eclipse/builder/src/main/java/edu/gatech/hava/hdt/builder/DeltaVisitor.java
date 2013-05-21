package edu.gatech.hava.hdt.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

class DeltaVisitor implements IResourceDeltaVisitor {

	private final HavaBuilder havaBuilder;

	DeltaVisitor(final HavaBuilder havaBuilder) {

		this.havaBuilder = havaBuilder;

	}

	/** {@inheritDoc} */
    @Override
    public boolean visit(final IResourceDelta delta) throws CoreException {

        final IResource resource = delta.getResource();

        switch (delta.getKind()) {
        case IResourceDelta.ADDED:
        case IResourceDelta.CHANGED:
            havaBuilder.checkHava(resource);
            break;
        default:
            break;
        }

        // return true to continue visiting children.
        return true;

    }

}
