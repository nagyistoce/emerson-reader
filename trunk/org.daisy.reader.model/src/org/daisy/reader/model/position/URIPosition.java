package org.daisy.reader.model.position;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.reader.util.URIStringParser;

public class URIPosition implements IAutonomousPosition {
	private String identifier;
	private URL referer;
	private String fragment;
	private URI absolute;
	
	/**
	 * Constructor.
	 * @param identifier A URI form identifier
	 * @param referer The resource in which identifier appears.
	 */
	public URIPosition(String identifier, URL referer) {
		if(identifier==null || referer == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		this.identifier = identifier;
		this.referer = referer;
	}
		
	/**
	 * Retrieve the unprocessed identifier as it 
	 * appears in the source referer.
	 * @return An identifier, never null.
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Retrieve the identifier as an Absolute URI
	 * @return An URI, never null.
	 * @throws URISyntaxException
	 */
	public URI getAbsoluteURI() throws URISyntaxException {
		if(absolute==null) {
			String fragment = URIStringParser.getFragment(identifier);
			String path = URIStringParser.stripFragment(identifier);
			URI dest = new URI(null, null, 
					(path!=null && path.length()>0)
						?path
							:null, 
								(fragment!=null && fragment.length()>0)
									?fragment
										:null);
			absolute = referer.toURI().resolve(dest);
		}
		return absolute;
	}
	
	/**
	 * Get the URL of the resource from which the identifier
	 * was retrieved.
	 * @return A URL, never null.
	 */
	public URL getReferer() {
		return referer;
	}
	
	/**
	 * Get the fragment of the identifier. The return value is interned.
	 * @return A fragment, or the empty string
	 * if the identifier contains no fragment.
	 */
	public String getFragment() {
		if(fragment==null) {
			fragment = URIStringParser.getFragment(identifier);
			if(fragment!=null)fragment.intern();
		}
		return fragment;
		
	}
	
	/**
	 * Get the local filename of the identifier. 
	 * <p>The name uses the syntax of a <strong>decoded</strong> URI string.</p>
	 * @return A filename, or the empty string
	 * if the identifier contains no filename.
	 */
	public String getFileName() {
		return URIStringParser.getFileLocalName(identifier);
	}
	
	@Override
	public String toString() {		
		return "URIPosition: " + this.identifier + " from " + this.referer.toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
