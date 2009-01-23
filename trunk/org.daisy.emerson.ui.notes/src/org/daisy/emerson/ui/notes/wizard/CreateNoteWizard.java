package org.daisy.emerson.ui.notes.wizard;

import java.util.Date;

import org.daisy.emerson.ui.notes.Activator;
import org.daisy.emerson.ui.notes.Messages;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.model.position.PositionTransformer;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.notes.Note;
import org.daisy.reader.notes.Notes;
import org.daisy.reader.util.User;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for creating a Note.
 * @author Markus Gylling
 */
public class CreateNoteWizard extends Wizard implements INewWizard {

	private CreateNotePage createPage;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	      setWindowTitle(Messages.CreateNoteWizard_AddNote);
	      setDefaultPageImageDescriptor(Activator.getImageDescriptor("icons/add_wiz.gif"));	       //$NON-NLS-1$
	}
	
	@Override
	public void addPages() {
		createPage = new CreateNotePage();	 
		addPage(createPage);		
	}

	@Override
	public boolean performFinish() {
		Model model = ModelManager.getModel();			
		try {						
			Notes.getInstance().add(
					new Note(
						getContext(model),
						new Date().getTime(),
						PositionTransformer.toAutonomousPosition(
								model.getPosition()),
						createPage.getNote(),
						(String)model.getProperty(
								IPropertyConstants.PUBLICATION_UUID),
						User.getID(),
						getSequence(model),
						(PublicationType)model.getProperty(
							IPropertyConstants.PUBLICATION_TYPE))
					);
		} catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
			return false;
		}		
		return true;		
	}

	private String getContext(Model model) {
		//Create a context string from INavigation
		//this doesnt necessarily need to be persisted
		//but could be recreated on instantiation
		INavigation nav = model.getNavigation();		
		INavigationItem currentHeading = nav.getCurrent(Semantic.HEADING);
		INavigationItem currentPage = nav.getCurrent(Semantic.PAGE_NUMBER);
		StringBuilder sb = new StringBuilder();
		if(currentHeading!=null)
			sb.append(currentHeading.getLabel());
		if(currentPage!=null) {
			sb.append(" ["); //$NON-NLS-1$
			sb.append(currentPage.getLabel());
			sb.append("]"); //$NON-NLS-1$
		}
		return sb.toString();
	}
	
	private int getSequence(Model model) {			
		INavigation nav = model.getNavigation();		
		INavigationItem currentHeading = nav.getCurrent(Semantic.HEADING);
		INavigationItem currentPage = nav.getCurrent(Semantic.PAGE_NUMBER);
		if(currentPage!=null) return currentPage.getOrdinal();
		return currentHeading.getOrdinal();
	}
	
	



}
