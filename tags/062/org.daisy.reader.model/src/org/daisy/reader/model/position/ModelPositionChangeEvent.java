package org.daisy.reader.model.position;

import java.util.EventObject;

import org.daisy.reader.model.Model;

public class ModelPositionChangeEvent extends EventObject {
		
	private Model model;		
	private IPosition newPosition;
		
	/** The model whose position changed.  */
	
	@Override
	public Model getSource() {
		return model;
	}

	/** The new position of the model */
	public IPosition getNewPosition() {
		return newPosition;
	}

	public ModelPositionChangeEvent(Model source, IPosition newPosition) {
		super(source);
		this.newPosition = newPosition;
		this.model = source;
	}

	private static final long serialVersionUID = -1540005699046932029L;
}
