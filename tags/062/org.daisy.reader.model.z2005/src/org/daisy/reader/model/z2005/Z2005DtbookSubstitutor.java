package org.daisy.reader.model.z2005;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.daisy.reader.model.dtb.IDtbTextContentSubstitutor;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.util.StreamTransformer;
import org.daisy.reader.util.TempDir;
import org.daisy.reader.util.URIStringParser;

/**
 * Utility class that provides HTMLized temp renders of DTBook files
 * for browser compatibility purposes.
 * <p>Reasons to use this approach:</p>
 * <ul>
 * <li>IDness and fragment URIs</li>
 * <li>Image, table display</li>
 * <li>Browser have no catalog lookups for DTBook DTDs</li>
 * @author Markus Gylling
 */

public class Z2005DtbookSubstitutor implements IDtbTextContentSubstitutor {
	File tempdir = null;
	Map<String,File> substitutes = null;
	private Z2005Model model;
	Set<String> errorFiles;
	
	public Z2005DtbookSubstitutor(Z2005Model model) {
		this.model = model;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.dtb.IDtbTextContentSubstitutor#substitute(java.net.URL)
	 */
	public URL substitute(URL original) {
		
		if(substitutes == null) {
			try {
				tempdir = TempDir.create((String)model.getProperty(IPropertyConstants.PUBLICATION_UUID));
				substitutes = new HashMap<String,File>();	
				errorFiles = new HashSet<String>();
			} catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
				tempdir = null;
			}
		}
		
		//avoid repeated attempts when initialization failed
		if(tempdir==null) return null;
		
		URI origURI = null;
		String path = null;
		try {
			origURI = original.toURI();
			path = origURI.getPath();
			
			//avoid repeated attempts when source parse failed
			if(errorFiles.contains(path)) return null;
			
			if(!substitutes.containsKey(path)) {				
				File dest = new File(tempdir,URIStringParser.getFileLocalName(path));		
				substitutes.put(path, StreamTransformer.transform(original, dest, getTransformConfig()));				
			}
			
			File substituteFile = substitutes.get(path);
			URI frag = new URI(null,null,null,origURI.getFragment());
			URI substituteURI = substituteFile.toURI().resolve(frag);
			return substituteURI.toURL();
			
		} catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			errorFiles.add(path);
			return null;
		}

	}

	void dispose() {
		if(tempdir!=null) {		
			try{
				File[] files = tempdir.listFiles();
				if(files!=null) {
					for (int i = 0; i < files.length; i++) {
						File f = files[i];
						f.delete();
					}
				}
				tempdir.delete();
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	
	Map<String, Object> dtbookConfig;

	private Map<String, Object> getTransformConfig() {

		if (dtbookConfig == null) {
			dtbookConfig = new HashMap<String, Object>();
			dtbookConfig.put(StreamTransformer.KEY_DTD, "");
			dtbookConfig.put(StreamTransformer.KEY_HTTP_EQUIV, Boolean.TRUE);
			dtbookConfig.put(StreamTransformer.KEY_FORCE_HTML_EXTENSION, Boolean.TRUE);
			
			ArrayList<String> uriAttrs = new ArrayList<String>();
			uriAttrs.add("src");
			uriAttrs.add("href");
			uriAttrs.add("smilref");
			dtbookConfig.put(StreamTransformer.KEY_MOD_RELATIVE_LINKS, uriAttrs);

			Map<String, String> nsMap = new HashMap<String, String>();
			nsMap.put(StreamTransformer.NAMESPACE_DTBOOK, StreamTransformer.NAMESPACE_XHTML);
			dtbookConfig.put(StreamTransformer.KEY_NAMESPACE_MAP, nsMap);

			Map<String, String> elemMap = new HashMap<String, String>();
			elemMap.put(StreamTransformer.ELEMENT_DTBOOK, StreamTransformer.ELEMENT_HTML);
			elemMap.put(StreamTransformer.ELEMENT_BOOK, StreamTransformer.ELEMENT_BODY);
			dtbookConfig.put(StreamTransformer.KEY_ELEMENT_MAP, elemMap);
		}
		return dtbookConfig;

	}
	
}
