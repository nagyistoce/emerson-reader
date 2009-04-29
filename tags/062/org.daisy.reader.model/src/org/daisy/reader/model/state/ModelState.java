package org.daisy.reader.model.state;

/**
 * The different states of an IModel instance.
 * @author Markus Gylling
 */
public enum ModelState {	
	LOADING, LOADED,
	READ_PREPARING,	READING, 
	STOPPING, STOPPED,
	RELOCATING, RELOCATED,
	DISPOSING, DISPOSED;
}

