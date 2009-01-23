package org.daisy.reader.model.position.memento;

import org.daisy.reader.Activator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Retrieve objects that can serialize and deserialize a given type of IPosition.
 * <p>Extension point id <code>org.daisy.reader.position.mementoSupport</code>.</p>
 * @author Markus Gylling
 */
public class PositionMementoSupportFactory {

	public static PositionMementoSupportFactory newInstance() {
		return new PositionMementoSupportFactory();
	}
	
	/**
	 * Locate and instantiate an IPositionMementoSupport that supports
	 * serializing and deserializing the given IPosition type.
	 * @param position An implementor of IPosition
	 * @return An IPositionMementoSupport or null if retrieval failed.
	 */
	public IPositionMementoSupport create(Class<?> positionType) {
		
		IPositionMementoSupport mementoSupport = null;
		IExtensionRegistry registry = Platform.getExtensionRegistry();								
		IExtensionPoint iep = registry.getExtensionPoint(IPositionMementoSupport.EXTENSION_POINT_ID);
		IExtension[] extensions = iep.getExtensions();
		
		for (int i = 0; i < extensions.length; i++) {
			try {
				IExtension ext = extensions[i];
				IConfigurationElement[] elems = ext.getConfigurationElements();
				for (int j = 0; j < elems.length; j++) {
					IConfigurationElement elem = elems[j];
					if(elem.getName().equals(MEMENTO_SUPPORT)) {
						String supportedPositionType = elem.getAttribute(SUPPORTS);	
						if(supportedPositionType.equals(positionType.getName())) {
							String klass = elem.getAttribute(CLASS);
							Bundle bundle = Platform.getBundle(ext.getContributor().getName());						
							Class<?> c = bundle.loadClass(klass);								
							mementoSupport = (IPositionMementoSupport)c.newInstance();
						}
					}														
				}
			} catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			} 
		}	
		if(mementoSupport==null) {
			System.err.println("PositionMementoSupportFactory#create: returning null");	//$NON-NLS-1$
		}
		return mementoSupport;
	}
	
	private static final String CLASS = "class"; //$NON-NLS-1$
	private static final String SUPPORTS = "supports"; //$NON-NLS-1$
	private static final String MEMENTO_SUPPORT = "mementoSupport"; //$NON-NLS-1$
}
