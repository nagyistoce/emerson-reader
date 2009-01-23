package org.daisy.reader.model.position.memento;

import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.IPosition;
import org.eclipse.ui.IMemento;

/**
 * Extension id <code>org.daisy.reader.position.mementoSupport</code>.
 * @author Markus Gylling
 */
public interface IPositionMementoSupport extends IPosition {

	static final String EXTENSION_POINT_ID = "org.daisy.reader.position.mementoSupport"; //$NON-NLS-1$
	/**
	 * Create an IAutonomousPosition from the given IMemento.
	 * <p>The reason we need IAutonomousPosition is that the Model
	 * to which the position refers may not be available at the 
	 * time of deserialization.</p> 
	 * @return 
	 */
	public IAutonomousPosition deserialize(IMemento source) throws Exception;
	
	/**
	 * Serialize the given IPosition into the given IMemento.
	 * <p>The object that does the serialization must also support
	 * deserializing the the result.</p>
	 */
	public void serialize(IPosition position, IMemento destination) throws Exception;
			
}
