package org.daisy.reader.model.audio;

import java.net.URL;

import org.daisy.reader.model.exception.UnsupportedContentTypeException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * A factory producing instances of IAudioKitFactory
 * using RCP plugin discovery. 
 * @author Markus Gylling
 */
public class AudioKitFactoryFactory {

	private static final String extensionPointID = "org.daisy.reader.model.audiokitfactory"; //$NON-NLS-1$
	
	private IExtension[] factories;
	
	public static AudioKitFactoryFactory newInstance() {
		return new AudioKitFactoryFactory();
	}
	
	/**
	 * Produce an instance of IAudioKit that can render
	 * the incoming audio type.
	 * @param audio The type of audio the IAudioKitFactory must support. 
	 * @throws UnsupportedContentTypeException if no IAudioKitFactory
	 * supporting the given audio type was found.
	 */
	public IAudioKitFactory newAudioKitFactory(URL audio) throws UnsupportedContentTypeException {
		
		IAudioKitFactory factory = null;
		
		if(factories==null) 
			factories = getFactories();		
					
		for (int i = 0; i < factories.length; i++) {
			IExtension ext = factories[i];
			IConfigurationElement[] elems = ext.getConfigurationElements();
			for (int j = 0; j < elems.length; j++) {
				IConfigurationElement elem = elems[j];
				if(elem.getName().equals("factory")) { //$NON-NLS-1$
					String klass = elem.getAttribute("class"); //$NON-NLS-1$
					try {
						Bundle bundle = Platform.getBundle(ext.getContributor().getName());						
						Class<?> c = bundle.loadClass(klass);								
						factory = (IAudioKitFactory)c.newInstance();
						if(factory.supportsContentType(audio)) {
							return factory;
						}
					} catch (InvalidRegistryObjectException e) {
						Activator.getDefault().logError(e.getMessage(),e);
					} catch (InstantiationException e) {
						Activator.getDefault().logError(e.getMessage(),e);
					} catch (IllegalAccessException e) {
						Activator.getDefault().logError(e.getMessage(),e);
					} catch (ClassNotFoundException e) {
						Activator.getDefault().logError(e.getMessage(),e);
					}
				}														
			}			
		}	
		throw new UnsupportedContentTypeException(audio.getPath());

	}

	private IExtension[] getFactories() {		
		IExtensionRegistry registry = Platform.getExtensionRegistry();								
		IExtensionPoint iep = registry.getExtensionPoint(extensionPointID);
		return iep.getExtensions();		
	}
}
