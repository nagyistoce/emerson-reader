package org.daisy.reader.ncx;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.reader.ncx.messages"; //$NON-NLS-1$	
	public static String NcxLoader_noPlayOrderError;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
