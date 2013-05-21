package edu.gatech.hava.hdt.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;

public class HavaBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "edu.gatech.hava.hdt.builder";

    private static final String MARKER_TYPE = "edu.gatech.hava.hdt.builder.marker.problem";

    class ResourceVisitor implements IResourceVisitor {

        /** {@inheritDoc} */
        @Override
        public boolean visit(final IResource resource) {
            checkHava(resource);
            //return true to continue visiting children.
            return true;
        }

    }

    public HavaBuilder() {

    }

    private static int[] getLineRange(final IFile file,
                                      final int lineNumber,
                                      final int column)
            throws CoreException, IOException {

        final InputStream stream = file.getContents();
        final BufferedReader reader = new CustomBufferedReader(stream);

        int start = 0;
        int end = 0;

        for (int i = 1; true; i++) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            int lineLength = line.length();
            if (i < lineNumber) {
                start += lineLength;
            } else {
                end = start + lineLength;
                break;
            }
        }

        return new int[]{start, end};

    }

    private void addMarker(final IFile file,
                           final HException e,
                           final int severity)
            throws IOException, CoreException {

        final String message = e.getMessage();
        final int lineNumber = e.getLine();

        final IMarker marker = file.createMarker(MARKER_TYPE);

        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.SEVERITY, severity);
        marker.setAttribute(IMarker.LINE_NUMBER,
                lineNumber == -1 ? 1 : lineNumber);

        if (lineNumber != -1) {
            int[] range = getLineRange(file, lineNumber, e.getColumn());
            marker.setAttribute(IMarker.CHAR_START, range[0]);
            marker.setAttribute(IMarker.CHAR_END, range[1]);
        }

    }

    /** {@inheritDoc} */
    @Override
    protected final IProject[] build(final int kind,
                                     final Map args,
                                     final IProgressMonitor monitor)
            throws CoreException {

        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }

        return null;

    }

    void checkHava(final IResource resource) {

        if (resource instanceof IFile && resource.getName().endsWith(".hava")) {
            IFile file = (IFile) resource;
            deleteMarkers(file);
            //HavaErrorHandler reporter = new HavaErrorHandler(file); //lol
            try {
                try {
                    //parse file
                    final HEngine engine = new HEngine();
                    engine.load(new InputStreamReader(file.getContents()));
                } catch (final HException e) {
                    addMarker(file, e, IMarker.SEVERITY_ERROR);
                }
            } catch (final IOException e) {
                ;
            } catch (final CoreException e) {
                ;
            }
        }

    }

    private void deleteMarkers(final IFile file) {

        try {
            file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
        } catch (final CoreException ce) {
            ;
        }

    }

    protected void fullBuild(final IProgressMonitor monitor)
            throws CoreException {

        try {
            getProject().accept(new ResourceVisitor());
        } catch (final CoreException e) {
            ;
        }

    }

    protected void incrementalBuild(final IResourceDelta delta,
                                    final IProgressMonitor monitor)
            throws CoreException {

        // the visitor does the work.
        delta.accept(new DeltaVisitor(this));

    }

}
