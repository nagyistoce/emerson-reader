package org.daisy.emerson.ui.library;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.emerson.ui.library.messages"; //$NON-NLS-1$
	public static String InfoSelectedHandler_Author;
	public static String InfoSelectedHandler_File;
	public static String InfoSelectedHandler_LastAccess;
	public static String InfoSelectedHandler_PublicationInformation;
	public static String InfoSelectedHandler_Title;
	public static String InfoSelectedHandler_Type;
	public static String LibraryView_Author;
	public static String LibraryView_Title;
	public static String LibraryView_Type;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
