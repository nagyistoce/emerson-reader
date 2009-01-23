package org.daisy.reader.notes;


public class NoteReplacedEvent extends NoteAddedEvent {

	public Note replacedNote;
	
	public NoteReplacedEvent(Object source, Note replacedNote, Note newNote) {
		super(source, newNote);
		this.replacedNote = replacedNote;
	}
	
	private static final long serialVersionUID = -8524724861860586988L;	
		
}
