package org.daisy.reader.model.adapt;


/**
 * Adapt objects to labels.
 * @author Markus Gylling
 */

public interface ILabelAdapter {
	/**
	 * Get the label of an object. 
	 * @param o The object to adapt.
	 */
	public String getLabel(Object o);
		
}
