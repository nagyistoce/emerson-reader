package org.daisy.reader.model.ops;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.reader.model.ops.messages"; //$NON-NLS-1$
	public static String OpsModel_epub;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
