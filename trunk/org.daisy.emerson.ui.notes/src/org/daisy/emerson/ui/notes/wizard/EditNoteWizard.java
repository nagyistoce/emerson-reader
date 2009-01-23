package org.daisy.emerson.ui.notes.wizard;

import org.daisy.emerson.ui.notes.Activator;
import org.daisy.emerson.ui.notes.Messages;
import org.daisy.reader.notes.Note;
import org.daisy.reader.notes.Notes;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for creating a marker.
 * @author Markus Gylling
 */
public class EditNoteWizard extends Wizard implements INewWizard {

	private EditNotePage editPage;
	private Note oldNote;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	      setWindowTitle(Messages.EditNoteWizard_EditNote);
	      setDefaultPageImageDescriptor(Activator.getImageDescriptor("icons/add_wiz.gif"));	 	       //$NON-NLS-1$
	      oldNote = (Note)selection.getFirstElement();
	}
	
	@Override
	public void addPages() {
		editPage = new EditNotePage(oldNote);	 
		addPage(editPage);		
	}

	@Override
	public boolean performFinish() {				
		if(editPage.getTextChanged()) {
			Note newNote = new Note(
					oldNote.getContext(),
					oldNote.getTimeStamp(),
					oldNote.getLocation(),
					editPage.getNote(),
					oldNote.getPublicationUID(),
					oldNote.getUserID(),
					oldNote.getSequence(),
					oldNote.getPublicationType()					
					);	
			Notes.getInstance().replace(oldNote, newNote);			
		}	
		return true;		
	}
	
}
