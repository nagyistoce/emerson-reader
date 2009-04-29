package org.daisy.reader.model.property;

import org.daisy.reader.model.Model;

/**
 * Manage dynamic properties of a certain Model type.
 * @author Markus Gylling
 */
public interface IModelPropertyManager {
	
	public final static String EXTENSION_POINT_ID = "org.daisy.reader.model.propertyManager"; //$NON-NLS-1$
	
	public void initialize(Model model);
	
	public void dispose();
		
}
