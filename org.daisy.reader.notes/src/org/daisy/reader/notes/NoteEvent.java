package org.daisy.reader.notes;

import java.util.EventObject;

public abstract class NoteEvent extends EventObject {

	public NoteEvent(Object source) {
		super(source);
	}
	
	private static final long serialVersionUID = -7176140190395680698L;
}
