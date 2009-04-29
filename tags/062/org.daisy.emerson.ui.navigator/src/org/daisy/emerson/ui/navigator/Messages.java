package org.daisy.emerson.ui.navigator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.emerson.ui.navigator.messages"; //$NON-NLS-1$
	public static String NavigateSelectedPageHandler_goToPage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
