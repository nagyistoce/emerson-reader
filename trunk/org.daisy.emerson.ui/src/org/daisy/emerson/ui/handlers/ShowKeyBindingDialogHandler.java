package org.daisy.emerson.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

public class ShowKeyBindingDialogHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
    	IBindingService bindingService = 
    		(IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);    	
    	bindingService.openKeyAssistDialog();    	    	
		return null;
	}

}
