package edu.gatech.hava.hdt.launch.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.gatech.hava.hdt.launch.HavaLaunchPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /** {@inheritDoc} */
    @Override
    public void initializeDefaultPreferences() {

        final IPreferenceStore store =
            HavaLaunchPlugin.getDefault().getPreferenceStore();

        store.setDefault(PreferenceConstants.P_MAX_MEMORY, 60);

    }

}
