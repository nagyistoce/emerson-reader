package org.daisy.emerson.ui.notes.wizard;

import org.daisy.emerson.ui.notes.Messages;
import org.daisy.reader.notes.Note;
import org.eclipse.swt.widgets.Composite;


public class EditNotePage extends NotePage {
	Note note;
	
	public EditNotePage(Note note) {
		super("EditNotePage"); //$NON-NLS-1$
		this.setTitle(Messages.EditNotePage_EditNote);
		this.setDescription(Messages.EditNotePage_EditNoteContent);
		this.note = note;
		setPageComplete(true);
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		text.setText(note.getContent());
	}
	
}
