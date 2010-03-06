package org.daisy.emerson.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.emerson.ui.messages"; //$NON-NLS-1$
	public static String UIPreferencePage_DefaultOpenPath;
	public static String AccessiblePresentationFactory_ViewMenu;
	public static String UIPreferencePage_ShowCoolBar;
	public static String UIPreferencePage_ForceFocusChange;
	public static String EmersonPopupTableDialog_EscToClose;
	public static String OpenPublicationHandler_AllFiles;
	public static String OpenPublicationHandler_EmersonContent;
	public static String OpenPublicationHandler_OpenBook;
	public static String OpenPublicationHandler_OpenFailed1;
	public static String OpenPublicationHandler_OpenFailed2;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
}
