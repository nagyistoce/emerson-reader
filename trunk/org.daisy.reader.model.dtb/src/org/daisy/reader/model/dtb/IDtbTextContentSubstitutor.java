package org.daisy.reader.model.dtb;

import java.net.URL;

public interface IDtbTextContentSubstitutor {

	public final static String KEY = "P_TEXT_CONTENT_SUBSTITUTION"; //$NON-NLS-1$
	
	/**
	 * Get a substitute DTB Textual Content URL, or null if substitution failed.
	 * @param original The URL to return a substitute for
	 */
	public URL substitute(URL input);
	
}
