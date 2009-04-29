package org.daisy.reader.model.state;

import java.util.EventListener;

/**
 * Interface for listening to ModelState change events raised by an IModel instance or the ModelManager.
 * @author Markus Gylling
 */
public interface IModelStateChangeListener extends EventListener { //, Listener
	public void modelStateChanged(ModelStateChangeEvent event);
}
