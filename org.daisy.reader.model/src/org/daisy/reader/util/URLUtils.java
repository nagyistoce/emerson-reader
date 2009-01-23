package org.daisy.reader.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.reader.Activator;

public class URLUtils {
		
	public static URL resolve (URL base, String ref) throws MalformedURLException {		
		URI dest = null;
		try {
			//dest = new URI(null, null, ref, null);			
			dest = new URI(null, null, URIStringParser.stripFragment(ref), URIStringParser.getFragment(ref));
			return base.toURI().resolve(dest).toURL();
		} catch (URISyntaxException e) {
			Activator.getDefault().logError(e.getMessage(), e);		
		}		
		return new URL(base, ref);
	}
		
	/**
	 * Convert a pathspec into a file URL.
	 * @param pathSpec
	 * @return
	 * @throws MalformedURLException 
	 */
	public static URL toFileURL(String pathSpec) throws MalformedURLException {		
		return new File(pathSpec).toURI().toURL();
	}
	
//  private static String slashify(String path, boolean isDirectory) {
//	String p = path;
//	if (File.separatorChar != '/')
//	    p = p.replace(File.separatorChar, '/');
//	if (!p.startsWith("/"))
//	    p = "/" + p;
//	if (!p.endsWith("/") && isDirectory)
//	    p = p + "/";
//	return p;
// }	
}
