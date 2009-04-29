package org.daisy.reader.model.adapt;

import java.net.URL;

/**
 * Adapt objects to URLs of textual resources.
 * <p>Typical clients of this adapter include browser widgets 
 * that display textual content.</p>
 * @author Markus Gylling
 */

public interface ITextURLAdapter {
	/**
	 * Get the URL of a textual resource.
	 * <p>The returned URL may include a fragment identifier.</p> 
	 * @param o The object to adapt.
	 */
	public URL getURL(Object o);
		
}
