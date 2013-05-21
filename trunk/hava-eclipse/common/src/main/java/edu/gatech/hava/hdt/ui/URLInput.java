package edu.gatech.hava.hdt.ui;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import edu.gatech.hava.hdt.core.resources.URLStorage;

public class URLInput implements IStorageEditorInput {

    private URLStorage storage;

    public URLInput(final String url,
                    final String pluginID)
            throws MalformedURLException {

        storage = new URLStorage(url, pluginID);

    }

    @Override
    public IStorage getStorage() {

        return storage;

    }

    @Override
    public boolean exists() {

        return storage.exists();

    }

    @Override
    public ImageDescriptor getImageDescriptor() {

        return null;

    }

    @Override
    public String getName() {

        return storage.getName();

    }

    @Override
    public IPersistableElement getPersistable() {

        return null;

    }

    @Override
    public String getToolTipText() {

        return "Remote file: " + storage.getName();

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(final Class adapter) {

        return null;

    }

    /**
     * {@inheritDoc}
     *
     * @return true if obj is a {@link URLInput} and the two
     *         objects have equal {@link URLStorage}s
     */
    @Override
    public boolean equals(final Object obj) {

        boolean equals;

        if (obj instanceof URLInput) {

            final URLInput otherInput = (URLInput) obj;

            if (storage == null && otherInput.storage == null) {
                equals = true;
            } else if (storage == null || otherInput.storage == null) {
                equals = false;
            } else {
                equals = storage.equals(otherInput.storage);
            }

        } else {

            equals = false;

        }

        return equals;

    }

    /**
     * {@inheritDoc}
     *
     * @return the hashCode of the underlying {@link URLStorage}.
     */
    @Override
    public int hashCode() {

        int hashCode;

        if (storage != null) {
            hashCode = storage.hashCode();
        } else {
            hashCode = 0;
        }

        return hashCode;

    }

}
