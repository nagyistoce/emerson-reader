package org.daisy.reader.notes;

public class NotesAddedEvent extends NoteEvent {

	public Note[] newNotes;
	
	public NotesAddedEvent(Object source, Note[] newNotes) {
		super(source);
		this.newNotes = newNotes;
	}
	
	private static final long serialVersionUID = 7244692033914995122L;	
	
}
