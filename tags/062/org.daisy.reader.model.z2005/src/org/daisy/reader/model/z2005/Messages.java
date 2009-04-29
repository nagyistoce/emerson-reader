package org.daisy.reader.model.z2005;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.reader.model.z2005.messages"; //$NON-NLS-1$
	public static String Z2005Model_daisy;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
