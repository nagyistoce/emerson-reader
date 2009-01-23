package org.daisy.reader.model;

import java.net.URL;
import java.util.List;

import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.eclipse.core.runtime.ListenerList;

/**
 * An instance of a particular type of content.
 * @author Markus Gylling
 */
public abstract class Model {
	
	private ListenerList stateChangeListeners;
	private ListenerList positionChangeListeners;
	private ModelState currentState;
	protected INavigation navigation;
	protected Metadata metadata;
	protected URL url;
	
	public Model(URL url, INavigation navigation, Metadata metadata) {
		this.url = url;
		this.metadata = metadata;
		this.navigation = navigation;
		this.stateChangeListeners = new ListenerList(ListenerList.IDENTITY);
		this.positionChangeListeners = new ListenerList(ListenerList.IDENTITY);	
		this.currentState = ModelState.DISPOSED;
	}
	
	/**
	 * Get an ordered list of document entities that make up the presentation.
	 */
	public abstract List<?> getSpine();
	
	/**
	 * Get the INavigation instance associated with this Model.
	 * @return the INavigation instance associated with this Model, or
	 * null if no association has been made.
	 */
	public INavigation getNavigation() {
		return navigation;
	}

	/**
	 * Does this Model instance support navigating using the given semantic?
	 * @param mode The Semantic for which navigation may be supported
	 * @return true if the Semantic is supported, else false
	 */
	public abstract boolean supportsNavigationMode(Semantic semantic);

	/**
	 * Relocate the current position to the given position, and start rendering.
	 * @return true if relocation to the given position succeeded, false otherwise
	 */
	public abstract boolean setPosition(IPosition position);
		
	/**
	 * Retrieve the Models current position. The return value is never null.
	 * The return value is detailed enough so that that <code>setPosition(getPosition())</code>
	 * results in absolutely zero positional change.
	 */
	public abstract IPosition getPosition();
	
	/**
	 * Retrieve a position with the given semantic 
	 * adjacent to the current position.
	 * <p>Note - a call to this method does not alter the
	 * current positional state of the Model.</p>
	 * @param direction The Direction to search in
	 * @param semantic The Semantic to search for
	 * @return An IPosition, or null of no such position could be retrieved.
	 */
	public abstract IPosition getAdjacentPosition(Direction direction, Semantic semantic);
	
	/**
	 * Start rendering from an unspecified position.
	 * <p>This means that the concrete instance should pick a rendering
	 * position based on it last recorded position.</p>
	 * <p>Subclasses with no notion of rendering should still update ModelState accordingly.</p>
	 */	
	public abstract boolean render();
	
	/**
	 * Stop rendering (playing), and maintain positional information.
	 * <p>If the Model is not currently rendering, this call has no effect.</p>
	 * <p>Subclasses with no notion of rendering should still update ModelState accordingly.</p> 
	 */
	public abstract boolean stop();
	
	/**
	 * Get the current state of this Model. This method never returns null.
	 */
	public ModelState getCurrentState() {
		return currentState;
	}
	
	protected void setCurrentState(ModelState newState) {
		currentState = newState;
	}
		
	/**
	 * Send a ModelStateChangeEvent to listeners, and also set
	 * the new state in the Model instance.
	 */
	protected void fireStateChangeEvent(ModelStateChangeEvent event) {
		setCurrentState(event.getNewState());
		Object[] listeners = stateChangeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
		 	((IModelStateChangeListener) listeners[i]).modelStateChanged(event);
		}		
	}
	
	/**
	 * Send a Position Change Event to listeners registered with this model
	 */
	public void firePositionChangeEvent(IPosition newPosition) {
		ModelPositionChangeEvent event = new ModelPositionChangeEvent(this,newPosition);
		/*
		 * Notify the INavigation separately before broadcasting the event
		 * to listeners, to make sure the entire model is updated properly.
		 * Note - any other future subpart of Model that depends on 
		 * positional state must have this favor done to it before broadcast. 
		 */
		this.navigation.positionChanged(event);
		
		Object[] listeners = positionChangeListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
		 	((IPositionChangeListener) listeners[i]).positionChanged(event);
		}			
	}
	
	/**
	 * Remove an {@link IModelStateChangeListener} from this Model instance.
	 * @param listener The listener to remove.
	 */
	void removeStateChangeListener(IModelStateChangeListener listener) {
		this.stateChangeListeners.remove(listener);
	}

	/**
	 * Add an {@link IModelStateChangeListener} from this Model instance.
	 * @param listener The listener to add.
	 */
	void addStateChangeListener(IModelStateChangeListener listener) {
		this.stateChangeListeners.add(listener);
	}
	
	/**
	 * Remove an {@link IPositionChangeListener} from this Model instance.
	 * @param listener The listener to remove.
	 */
	protected void removePositionChangeListener(IPositionChangeListener listener) {
		this.positionChangeListeners.remove(listener);
	}

	/**
	 * Add an {@link IPositionChangeListener} from this Model instance.
	 * @param listener The listener to add.
	 */
	protected void addPositionChangeListener(IPositionChangeListener listener) {
		this.positionChangeListeners.add(listener);
	}
	
	/**
	 * Release any resources connected to this instance.
	 */
	public void dispose() {
		fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.DISPOSING));		
		positionChangeListeners.clear();
		doDispose();		
		fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.DISPOSED));
		stateChangeListeners.clear();		
	}
	
	/**
	 * Convenience method.
	 * @return true if current ModelState is DISPOSING or DISPOSED.
	 */
	public boolean isDisposed() {
		return this.currentState== ModelState.DISPOSING
			|| this.currentState== ModelState.DISPOSED;
	}
	
	/**
	 * Method called when this model is about to be unloaded. 
	 * Release any resources connected to this instance, persist 
	 * any data to be persisted. For subclasses to implement.
	 */
	protected abstract void doDispose();
	
	/**
	 * Query the Model instance on a property.
	 * <p>Note - user agents may use the returned
	 * property's toString() method to display information to users.</p>
	 * <p>IPropertyConstants defines universal properties that must be recognized.</p> 
	 * @param query The ID of the property whose value is sought. 
	 * 
	 * @return The property, or null if this property is not set.
	 * @throws PropertyException If this property is not recognized.  
	 *  
	 */
	public Object getProperty(String key) throws PropertyException {
		if(IPropertyConstants.PUBLICATION_UUID.equals(key)) {
			return metadata.get(Metadata.UUID);
		}else if(IPropertyConstants.PUBLICATION_TITLE.equals(key)) {
			return metadata.get(Metadata.TITLE);
		}else if(IPropertyConstants.PUBLICATION_AUTHOR.equals(key)) {
			return metadata.get(Metadata.AUTHOR);			
		}else if(IPropertyConstants.PUBLICATION_URL.equals(key)) {
			return url;
		}
		throw new PropertyException(key);
	}

	/**
	 * Set a property on this Model instance.
	 * @throws PropertyException If this property is not supported.  
	 */
	public abstract void setProperty(String key, Object value) throws PropertyException;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Type: " + this.getProperty(IPropertyConstants.PUBLICATION_TYPE).toString()); //$NON-NLS-1$
		} catch (PropertyException e) {}
		sb.append("\nSpine: " + this.getSpine().toString()); //$NON-NLS-1$
		sb.append("\nMetadata: " + this.metadata.toString()); //$NON-NLS-1$
		sb.append("\nNavMap: " + this.navigation.toString()); //$NON-NLS-1$
		
		return sb.toString();
	}
}
