package edu.gatech.hava.hdt.core.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import edu.gatech.hava.hdt.core.runtime.URLStatus;

public class URLStorage implements IStorage {

    private URL url;
    private String pluginID;

    public URLStorage(final String url,
                      final String pluginID)
            throws MalformedURLException {

        this(new URL(url), pluginID);

    }

    public URLStorage(final URL url,
                      final String pluginID) {

        this.url = url;
        this.pluginID = pluginID;

    }

    @Override
    public InputStream getContents() throws CoreException {

        final InputStream stream;

        try {
            stream = url.openStream();
        } catch (final IOException e) {
            throw new CoreException(new URLStatus(e, pluginID));
        }

        return stream;

    }

    @Override
    public IPath getFullPath() {

        final Path p = new Path(url.toExternalForm());

        return p;

    }

    @Override
    public String getName() {

        String name = null;

        final String[] pieces = url.getPath().split("/");

        if (pieces.length > 0) {
            name = pieces[pieces.length - 1];
        }

        return name;

    }

    @Override
    public boolean isReadOnly() {

        return true;

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(final Class adapter) {

        return null;

    }

    public boolean exists() {

        try {

            URLConnection con = url.openConnection();
            String stat = con.getHeaderField(0);
            if (!stat.matches(".*200 OK.*")) {
                return false;
            }

        } catch (final Exception e) {
            return false;
        }

        return true;

    }

    /**
     * {@inheritDoc}
     *
     * @return true if obj is a {@link URLStorage} and
     *         the two objects have equal {@link URL}s.
     */
    @Override
    public boolean equals(final Object obj) {

        boolean equals;

        if (obj instanceof URLStorage) {

            final URLStorage otherStorage = (URLStorage) obj;

            if (url == null && otherStorage.url == null) {
                equals = true;
            } else if (url == null || otherStorage.url == null) {
                equals = false;
            } else {
                equals = url.equals(otherStorage.url);
            }

        } else {

            equals = false;

        }

        return equals;

    }

    /**
     * {@inheritDoc}
     *
     * @return the hashCode of the underlying {@link URL}.
     */
    @Override
    public int hashCode() {

        int hashCode;

        if (url != null) {
            hashCode = url.hashCode();
        } else {
            hashCode = 0;
        }

        return hashCode;

    }

}
