package org.daisy.reader.model.property;

/**
 * Global (not implementation dependant) property IDs that all Model 
 * implementations must recognize via Model#getProperty.
 * @author Markus Gylling
 */
public interface IPropertyConstants {
	
	/**
	 * A unique identifier of the publication. Return type is String. Never null.
	 */
	public static final String PUBLICATION_UUID = "PUBLICATION_UUID"; //$NON-NLS-1$

	/**
	 * A human-readable title of the publication. Return type is String. May be null.
	 */
	public static final String PUBLICATION_TITLE = "PUBLICATION_TITLE"; //$NON-NLS-1$
	
	/**
	 * A human-readable title of the publication. Return type is String. May be null.
	 */
	public static final String PUBLICATION_AUTHOR = "PUBLICATION_AUTHOR"; //$NON-NLS-1$
	
	/**
	 * A URL to the manifest of the publication. Return type is URL. Never null.
	 */
	public static final String PUBLICATION_URL = "PUBLICATION_URL"; //$NON-NLS-1$

	/**
	 * A human readable label for the type of publication. Return type is PublicationType. Never null.
	 */
	public static final String PUBLICATION_TYPE = "PUBLICATION_TYPE"; //$NON-NLS-1$
	
	/**
	 * A time measure of the total length of the publication. Return type is SmilClock. May be null.
	 */
	public static final String PUBLICATION_DURATON = "PUBLICATION_DURATION"; //$NON-NLS-1$
	
}
