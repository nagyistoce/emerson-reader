package org.daisy.reader.notes;

public class NoteAddedEvent extends NoteEvent {

	public Note newNote;
	
	public NoteAddedEvent(Object source, Note newNote) {
		super(source);
		this.newNote = newNote;
	}
	
	private static final long serialVersionUID = 8237221725688650926L;	
	
}
