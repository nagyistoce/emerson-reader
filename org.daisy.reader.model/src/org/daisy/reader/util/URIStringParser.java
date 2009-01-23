package org.daisy.reader.util;

public class URIStringParser {
	
	private static final char hash = '#';
	private static final char separator = '/';
	
	public static String stripFragment(String uri) {				
		StringBuilder sb = new StringBuilder();		
		for (int i = 0; i < uri.length(); i++) {
			if (uri.charAt(i)==hash) {
				return sb.toString();
			}
			sb.append(uri.charAt(i));			
		}
		return sb.toString();								
	}

	/**
	 * Get the base (parent directory) of a URI.
	 */
	public static String getBase(String uri) {		
		try{
			return uri.substring(0, uri.lastIndexOf(separator)+1);
		}catch (IndexOutOfBoundsException e) {
			return uri;
		}
	}
	
	public static String getFragment(String uri) {					
		StringBuilder sb = new StringBuilder();	
		int hashPos = -1;		
		for (int i = 0; i < uri.length(); i++) {
			if (uri.charAt(i)==hash) {
				hashPos = i;
			}else{
				if (hashPos > -1) sb.append(uri.charAt(i));
			}  
		}
		return sb.toString();								
	}
	
	public static String getFileLocalName(String uri) {
		StringBuilder sb = new StringBuilder();	
		for (int i = 0; i < uri.length(); i++) {
			if (uri.charAt(i)==hash) {
				return sb.toString();
			}else if (uri.charAt(i)==separator) {
				sb.delete(0, sb.length());
				continue;
			}
			sb.append(uri.charAt(i));			
		}
		return sb.toString();
	}

}
