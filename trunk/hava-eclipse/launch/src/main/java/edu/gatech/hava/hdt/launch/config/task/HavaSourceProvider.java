package edu.gatech.hava.hdt.launch.config.task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import edu.gatech.hava.parser.io.DefaultSourceProvider;
import edu.gatech.hava.parser.io.HSourceAddress;
import edu.gatech.hava.parser.io.HSourceProvider;

/**
 * {@link HSourceProvider} for the Hava launch configuration.
 */
class HavaSourceProvider extends DefaultSourceProvider {

    /* In the future, this could configured to retrieve files
     * more efficienty (for example, by caching remote files). */

    /**
     * Fetches relative imports from the workspace.
     *
     * {@inheritDoc}
     */
    @Override
    protected Reader getFileReader(final HSourceAddress address)
            throws IOException {

        final IPath path = new Path(address.getAddress());

        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        final Reader reader;

        try {
            reader = new InputStreamReader(file.getContents());
        } catch (final CoreException e) {
            throw new IOException(e);
        }

        return reader;

    }

}
