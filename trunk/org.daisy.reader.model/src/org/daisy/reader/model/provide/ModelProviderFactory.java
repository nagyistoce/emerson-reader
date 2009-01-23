package org.daisy.reader.model.provide;

import org.daisy.reader.model.exception.ModelFactoryException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.osgi.framework.Bundle;

public class ModelProviderFactory {
		
	private static final String PROVIDER_ELEM = "provider"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_ID_ATTR = "contentTypeId"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_ELEM = "contentType"; //$NON-NLS-1$

	public static ModelProviderFactory newInstance() {
		return new ModelProviderFactory();
	}
	
	/**
	 * Query the registry to find a plugin that exposes an IModelProvider that supports
	 * creating a model given this content type. 
	 * @return an IModelProvider instance, or null if none supporting the content type could be found.
	 * @throws ModelFactoryException If something goes wrong. 
	 */		
	public IModelProvider create(IContentType type) throws ModelFactoryException {
		IModelProvider provider = null;
		IExtensionRegistry registry = Platform.getExtensionRegistry();								
		IExtensionPoint iep = registry.getExtensionPoint(IModelProvider.EXTENSION_POINT_ID);
		IExtension[] modelProviders = iep.getExtensions();
		
		for (int i = 0; i < modelProviders.length; i++) {
			try {
				IExtension ext = modelProviders[i];
				IConfigurationElement[] elems = ext.getConfigurationElements();
				for (int j = 0; j < elems.length; j++) {
					IConfigurationElement elem = elems[j];
					if(elem.getName().equals(PROVIDER_ELEM)) {
						IConfigurationElement[] types = elem.getChildren(CONTENT_TYPE_ELEM);												
						for (int k = 0; k < types.length; k++) {
							if(type.getId().equals(types[k].getAttribute(CONTENT_TYPE_ID_ATTR))) {
								String klass = elem.getAttribute(CLASS_ATTR);
								Bundle bundle = Platform.getBundle(ext.getContributor().getName());						
								Class<?> c = bundle.loadClass(klass);								
								provider = (IModelProvider)c.newInstance();
								return provider;
							}
						}
					}														
				}
			} catch (InvalidRegistryObjectException e) {
				throw new ModelFactoryException(e.getMessage(),e);
			} catch (InstantiationException e) {
				throw new ModelFactoryException(e.getMessage(),e);
			} catch (IllegalAccessException e) {
				throw new ModelFactoryException(e.getMessage(),e);
			} catch (ClassNotFoundException e) {
				throw new ModelFactoryException(e.getMessage(),e);
			}
		}	
		return null;
	}
}
