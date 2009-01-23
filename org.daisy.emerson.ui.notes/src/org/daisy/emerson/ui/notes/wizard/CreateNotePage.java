package org.daisy.emerson.ui.notes.wizard;

import org.daisy.emerson.ui.notes.Messages;

public class CreateNotePage extends NotePage {
		
	public CreateNotePage() {
		super("NewNotePage"); //$NON-NLS-1$
		this.setTitle(Messages.CreateNotePage_NewNote);
		this.setDescription(Messages.CreateNotePage_AddNewNote);
		setPageComplete(true);
	}
	
}
