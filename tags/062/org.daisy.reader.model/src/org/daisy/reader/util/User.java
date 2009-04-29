package org.daisy.reader.util;

public class User {

	/**
	 * Get a UID of the current system user.
	 * <p>If system retrieval fails, return the string "default user".
	 */
	public static String getID() {
		return getID(false);
	}
	
	/**
	 * Get a UID of the current system user.
	 * @param throwOnFailure If system retrieval fails and false, 
	 * return the string "default user". If true, throw NPE.
	 */
	public static String getID(boolean throwOnFailure) {
		String id = System.getProperty("user.name"); //$NON-NLS-1$
		if(id==null) {
			if(throwOnFailure)
				throw new NullPointerException("user.name"); //$NON-NLS-1$
			id = "default user"; //$NON-NLS-1$
		}
		return id;
	}
	
}
