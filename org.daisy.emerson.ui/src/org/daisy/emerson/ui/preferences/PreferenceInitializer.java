package org.daisy.emerson.ui.preferences;

import org.daisy.emerson.ui.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();		
		store.setDefault(PreferenceConstants.P_DEFAULT_OPEN_PATH, System.getProperty("user.home")); //$NON-NLS-1$		
		store.setDefault(PreferenceConstants.P_SHOW_COOLBAR, false);
		store.setDefault(PreferenceConstants.P_FORCE_FOCUS_CHANGE, true);
	}

}
