package org.daisy.reader.notes;


public class NoteDeletedEvent extends NoteEvent {

	public Note deletedNote;
	
	public NoteDeletedEvent(Object source, Note deletedNote) {
		super(source);
		this.deletedNote = deletedNote;
	}
	
	private static final long serialVersionUID = -450893122189590673L;		
	
}
