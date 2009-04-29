package org.daisy.emerson.ui.dtb.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

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
		IPreferenceStore store = PlatformUI.getPreferenceStore();

		//skippability
		store.setDefault(PreferenceConstants.P_SKIP_PAGES,false);
		store.setDefault(PreferenceConstants.P_SKIP_FOOTNOTES,false);		
		store.setDefault(PreferenceConstants.P_SKIP_PRODNOTES,false);		
		store.setDefault(PreferenceConstants.P_SKIP_SIDEBARS,false);
	}

}
