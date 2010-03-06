package org.daisy.emerson.ui.browser.delegate;

import org.daisy.emerson.ui.browser.BrowserView;

/**
 * Enhance the behavior of the Emerson BrowserView.
 * <p>This interface imposes no restrictions on the nature of the enhancement service. 
 * Note that the lifecycle of an IBrowserService is controlled by the owning BrowserView.</p>
 * <p>See the <code>org.daisy.emerson.ui.browser.behaviorDelegate</code> extension point.</p>
 * @author Markus Gylling
 */
public interface IBrowserBehaviorDelegate {

	/**
	 * Initialize this IBrowserService.
	 * <p>This method is invoked immediately after the construction of this IBrowserService.</p>
	 * @param browserView The consumer of the services that this IBrowserService provides.
	 * @return a usable IBrowserService, or null if this IBrowserService was not initialized
	 * properly due to some contextual reason. For example, an IBrowserService may return null on
	 * this method if it does not apply to the currently loaded model.
	 */
	public IBrowserBehaviorDelegate initialize(BrowserView browserView);
	
	/**
	 * Dispose this IBrowserService.
	 */
	public void dispose();
	
	/**
	 * True if this IBrowserService is disposed.
	 */
	public boolean isDisposed();
	
}
