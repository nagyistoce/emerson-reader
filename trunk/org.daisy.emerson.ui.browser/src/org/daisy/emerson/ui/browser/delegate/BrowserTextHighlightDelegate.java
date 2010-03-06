package org.daisy.emerson.ui.browser.delegate;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.browser.BrowserView;
import org.daisy.emerson.ui.browser.IBrowserTargetChangeListener;

/**
 * A javascript-based text highlight delegate.
 * @author Markus Gylling
 */
public class BrowserTextHighlightDelegate implements IBrowserBehaviorDelegate, IBrowserTargetChangeListener {
	
	private BrowserView browserView;
	private final StringBuilder sb;
	private boolean isDisposed = false;

	/**
	 * The original background color of the current fragment. Defaults to white.
	 */
	private String currentFragmentBgColor = BGCOLOR_DEFAULT;

	/**
	 * The default background color of a highlighted fragment.
	 */
	private static final String BGCOLOR_DEFAULT = "rgb(255,255,255)";
	
	/**
	 * The default highlight color of a fragment.
	 */
	private static final Object BGCOLOR_HIGHLIGHT = "rgb(230,230,230)"; //TODO user configurable
	
	
	public BrowserTextHighlightDelegate() {		
		sb = new StringBuilder();			
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#initialize(org.daisy.emerson.ui.browser.BrowserView)
	 */
	public IBrowserBehaviorDelegate initialize(BrowserView browserView) {
		this.browserView = browserView;
		browserView.addTargetChangeListener(this);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#dispose()
	 */
	public void dispose() {
		if(browserView!=null) {
			browserView.removeTargetChangeListener(this);
		}	
		isDisposed = true;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.IBrowserTargetChangeListener#preTargetChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void preTargetChange(String newBaseURL, String prevBaseURL,
			String newFragment, String prevFragment) {
		
		if(browserView==null) return;
		
		//reset the current fragments bgcolor to its original
		
		if(prevFragment==null) return;				 								
		sb.append("document.getElementById('");
		sb.append(prevFragment);
		sb.append("').style.backgroundColor='");
		sb.append(currentFragmentBgColor);
		sb.append("';");
		browserView.execute(sb.toString());
		sb.delete(0, sb.length());
						
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.IBrowserTargetChangeListener#postTargetChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void postTargetChange(String newBaseURL, String prevBaseURL,
			String newFragment, String prevFragment) {
		
		if(browserView == null || newFragment==null) return;
		
		//store the bgcolor of the new fragment										
		try{
			sb.append("return document.getElementById('"); //$NON-NLS-1$
			sb.append(newFragment);
			sb.append("').style.backgroundColor"); //$NON-NLS-1$
			Object bgcolor = browserView.evaluate(sb.toString());
			if(bgcolor!=null) {
				currentFragmentBgColor = (String)bgcolor;
			}else {
				currentFragmentBgColor = BGCOLOR_DEFAULT;
			}
			sb.delete(0, sb.length());
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			currentFragmentBgColor = BGCOLOR_DEFAULT;
		}
		
		try{
			//set the highlight color on the incoming fragment
			sb.append("document.getElementById('"); //$NON-NLS-1$
			sb.append(newFragment);
			sb.append("').style.backgroundColor='"); //$NON-NLS-1$
			sb.append(BGCOLOR_HIGHLIGHT);
			sb.append("';"); //$NON-NLS-1$
			browserView.execute(sb.toString());
			sb.delete(0, sb.length());
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#isDisposed()
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

}
