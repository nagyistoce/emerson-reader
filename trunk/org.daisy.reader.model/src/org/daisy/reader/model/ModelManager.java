package org.daisy.reader.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.daisy.reader.model.exception.ModelFactoryException;
import org.daisy.reader.model.exception.ModelInstantiationException;
import org.daisy.reader.model.exception.UnsupportedContentTypeException;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.property.IModelPropertyManager;
import org.daisy.reader.model.property.PropertyManagerFactory;
import org.daisy.reader.model.provide.IModelProvider;
import org.daisy.reader.model.provide.ModelProviderFactory;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

/**
 * Static access point to the currently loaded model, 
 * and acting proxy for listeners that are propagated
 * to Models as they are loaded. 
 * 
 * @author Markus Gylling
 */
public class ModelManager {

	/** Currently active Model instance, may be null */
	private static Model activeModel = null;
	
	/** Currently active IModelPropertyManager instance, may be null */
	private static IModelPropertyManager activeModelPropertyManager = null;
		
	private static ListenerList modelStateChangeListenerProxy 
		= new ListenerList(ListenerList.IDENTITY);
		
	private static ListenerList modelPositionChangeListenerProxy 
		= new ListenerList(ListenerList.IDENTITY);
	
	
			
	/**
	 * Get the currently active {@link Model} instance, or
	 * null if no Model is loaded.
	 * @return The active Model, or null if no Model is loaded.</p>
	 */
	public static Model getModel() {
		return activeModel;
	}
			
	/**
	 * Have the ModelManager locate and create a Model instance that can
	 * represent the given content. 
	 * @param content The URL of the content to attempt to load
	 * <p>If no exception is thrown, the model was
	 * loaded successfully. The loaded Model is made active.</p> 
	 * @throws UnsupportedContentTypeException If no implementation supporting the given content was found.
	 * @throws ModelInstantiationException If model instantiation failed because of an error, possibly an error in the input content.
	 * @throws ModelFactoryException If the model factory or extension points are in bad shape.
	 */
	public static void load(URL content) throws UnsupportedContentTypeException, ModelInstantiationException, ModelFactoryException {
		//if we cant allocate a new Model instance for whatever reason, 
		//the currently loaded one (if any) should remain loaded. But we still
		//throw any exception.
		
		try{			
			//find a provider
			ModelProviderFactory factory = ModelProviderFactory.newInstance();		
			IModelProvider provider = factory.create(getContentType(content));
			if(provider==null) 
				throw new UnsupportedContentTypeException(
						content.getPath(), 
							new IllegalArgumentException());
	
			//if current and new are the same, close current to avoid
			//temp resource clashes etc. Else we unload only after
			//successfully having completed the new model load 
			if(activeModel!=null && !activeModel.isDisposed()					
					&& activeModel.url.getPath().equals(content.getPath())) 
				unload();
			
			//let the provider create the model
			fireEvent(new ModelStateChangeEvent(ModelManager.class,ModelState.LOADING));				
			Model model = provider.create(content);
			
			//propagate the listener proxies to the model
			propagateListeners(model);		
			
			//unload current, if any
			unload();
									
			//add and activate the new model
			activeModel = model;
			
			// Get a new model property manager; this may return null, 
			//which is fine, property management is optional
			activeModelPropertyManager 
				= PropertyManagerFactory.newInstance().create(activeModel);
						
			activeModel.fireStateChangeEvent(
					new ModelStateChangeEvent(activeModel,ModelState.LOADED));
			
		}catch (Exception e) {
			throw new ModelInstantiationException(e.getLocalizedMessage(),e);
		}				
	}
	
	private static void propagateListeners(Model model) {
		Object[] listeners = modelStateChangeListenerProxy.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
		  model.addStateChangeListener((IModelStateChangeListener) listeners[i]);
		}
		listeners = modelPositionChangeListenerProxy.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
		  model.addPositionChangeListener((IPositionChangeListener) listeners[i]);
		}
		
	}

	/**
	 * Dispose the currently loaded Model and remove it from this manager.
	 */
	public static void unload() {
		//close any priorly loaded model
		if(activeModel!=null && !activeModel.isDisposed()) {
			if(activeModel.getCurrentState()==ModelState.READING) {
				activeModel.stop();
			}
			activeModel.dispose();			
		}
		activeModel = null;
		
		//reset the models property manager
		if(activeModelPropertyManager!=null) {
			activeModelPropertyManager.dispose();
			activeModelPropertyManager = null;
		}
	}
	
	private static IContentType getContentType(URL path) throws IOException, URISyntaxException {
				
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = null;
		InputStream is = null;
		try{
			is = path.openStream();
			contentType = contentTypeManager.findContentTypeFor(
					is, new File(path.toURI()).getName());
		}finally{
			is.close();
		}
		return contentType;
	}
	
	/**
	 * Register as a listener to changes in the active Model's state.
	 * A listener registered here is propagated to all loaded Models,
	 * regardless of whether the Models were registered before or after
	 * the IModelStateChangeListener was registered. 
	 * @param listener
	 */
	public static void addStateChangeListener(IModelStateChangeListener listener) {		
		if(activeModel!=null)activeModel.addStateChangeListener(listener);
		modelStateChangeListenerProxy.add(listener);
	}
	
	/**
	 * Unregister as a listener to changes in the active Model's state.
	 */
	public static void removeStateChangeListener(IModelStateChangeListener listener) {
		if(activeModel!=null) activeModel.removeStateChangeListener(listener);	
		modelStateChangeListenerProxy.remove(listener);
	}
	
	/**
	 * Register as a listener to changes in the active Model's position.
	 * A listener registered here is propagated to all loaded Models,
	 * regardless of whether the Models were registered before or after
	 * the IPositionChangeListener was registered. 
	 * @param listener
	 */
	public static void addPositionChangeListener(IPositionChangeListener listener) {		
		if(activeModel!=null)activeModel.addPositionChangeListener(listener);
		modelPositionChangeListenerProxy.add(listener);
	}
	
	/**
	 * Unregister as a listener to changes in the active Model's position.
	 */
	public static void removePositionChangeListener(IPositionChangeListener listener) {
		if(activeModel!=null)activeModel.removePositionChangeListener(listener);	
		modelPositionChangeListenerProxy.remove(listener);
	}
		
	private static void fireEvent(ModelStateChangeEvent e) {
		 Object[] listeners = modelStateChangeListenerProxy.getListeners();
		 for (int i = 0; i < listeners.length; ++i) {
		 	((IModelStateChangeListener) listeners[i]).modelStateChanged(e);
		 }		
	}

}
