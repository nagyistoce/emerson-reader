package org.daisy.emerson.ui.notes.handlers;

import org.daisy.emerson.ui.notes.wizard.EditNoteWizard;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class EditNoteHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {				
		Model model = ModelManager.getModel();	
		if(model!=null && !model.isDisposed()) {
			 //Just launch the wizard and perform the edit in #performFinish()			
			EditNoteWizard wizard = new EditNoteWizard();	
			wizard.init(HandlerUtil.getActiveWorkbenchWindowChecked(event)
				.getWorkbench(), (IStructuredSelection)HandlerUtil.getCurrentSelectionChecked(event));			
			WizardDialog dialog = new WizardDialog(HandlerUtil
				.getActiveShell(event), wizard);			
			dialog.open();
		}
		return null;
	}
}
