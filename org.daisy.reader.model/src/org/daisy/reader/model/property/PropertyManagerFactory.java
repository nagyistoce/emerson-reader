package org.daisy.reader.model.property;

import org.daisy.reader.Activator;
import org.daisy.reader.model.Model;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PropertyManagerFactory {

	public static PropertyManagerFactory newInstance() {
		return new PropertyManagerFactory();
	}
	
	/**
	 * Create an {@link IModelPropertyManager} for the given model.
	 * @param model
	 * @return An {@link IModelPropertyManager}, or null.
	 */
	public IModelPropertyManager create(Model model) {
		IModelPropertyManager manager = null;
		IExtensionRegistry registry = Platform.getExtensionRegistry();								
		IExtensionPoint iep = registry.getExtensionPoint(IModelPropertyManager.EXTENSION_POINT_ID);
		if(iep == null) return null;
		IExtension[] managers = iep.getExtensions();
		
		for (int i = 0; i < managers.length; i++) {			
				IExtension ext = managers[i];
				IConfigurationElement[] elems = ext.getConfigurationElements();				
				for (int j = 0; j < elems.length; j++) {
					IConfigurationElement elem = elems[j];
					
					if(elem.getName().equals(PROPERTY_MANAGER)) {
						String supports = elem.getAttribute(FOR);						
						if(supports!=null && supports.equals(model.getClass().getName())) {
							try {
								String klass = elem.getAttribute(CLASS);
								Bundle bundle = Platform.getBundle(ext.getContributor().getName());						
								Class<?> c = bundle.loadClass(klass);								
								manager = (IModelPropertyManager)c.newInstance();
								manager.initialize(model);
								return manager;
							} catch (Exception e) {
								Activator.getDefault().logError(e.getLocalizedMessage(), e);
							}
						}	
					}														
				}	
		}	
		return null;
	}
	
	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String FOR = "for"; //$NON-NLS-1$
	private static final String PROPERTY_MANAGER = "propertyManager"; //$NON-NLS-1$
}