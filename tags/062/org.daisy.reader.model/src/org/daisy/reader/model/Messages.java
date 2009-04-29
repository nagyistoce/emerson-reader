package org.daisy.reader.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.reader.model.messages"; //$NON-NLS-1$
	public static String ModelFactoryException_prefix;
	public static String ModelInstantiationException_prefix;
	public static String UnsupportedContentTypeException_prefix;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
