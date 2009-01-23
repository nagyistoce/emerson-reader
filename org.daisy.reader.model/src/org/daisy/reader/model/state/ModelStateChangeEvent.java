package org.daisy.reader.model.state;

import java.util.EventObject;

import org.daisy.reader.model.Model;

public class ModelStateChangeEvent extends EventObject {
	
	private Model model;		
	private ModelState newState;
		
	/** The model whose state changed. May be null. */
	@Override
	public Model getSource() {
		return model;
	}

	/** The new state of the model whose state changed */
	public ModelState getNewState() {
		return newState;
	}

	public ModelStateChangeEvent(Model source, ModelState newState) {
		super(source);
		this.newState = newState;
		this.model = source;
	}

	public ModelStateChangeEvent(Object source, ModelState newState) {
		super(source);
		this.newState = newState;		
	}
	
	private static final long serialVersionUID = -3789136854835329104L;

}
