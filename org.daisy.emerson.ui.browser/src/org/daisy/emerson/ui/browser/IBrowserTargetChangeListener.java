package org.daisy.emerson.ui.browser;

public interface IBrowserTargetChangeListener {

	/**
	 * Method invoked by the BrowserView before the browser changes its target URL.
	 * @param newBaseURL The URL of the new target excluding the fragment identifier.
	 * @param prevBaseURL The URL of the previous target excluding the fragment. May be null.
	 * @param newFragment The fragment of the new URL. May be null if the set target does not 
	 * contain a fragment.
	 * @param prevFragment The fragment of the previous URL. May be null if the previous target did not 
	 * contain a fragment.
	 */
	public void preTargetChange(String newBaseURL, String prevBaseURL, String newFragment, String prevFragment);
	
	/**
	 * Method invoked by the BrowserView after the browser changed its target URL.
	 * @param newBaseURL The URL of the new target excluding the fragment identifier.
	 * @param prevBaseURL The URL of the previous target excluding the fragment. May be null.
	 * @param newFragment The fragment of the new URL. May be null if the set target does not 
	 * contain a fragment.
	 * @param prevFragment The fragment of the previous URL. May be null if the previous target did not 
	 * contain a fragment.
	 */
	public void postTargetChange(String newBaseURL, String prevBaseURL, String newFragment, String prevFragment);
	
}
