package org.daisy.emerson.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Close a view. This is a separate command+handler for accessibility reasons (need to repeat in main and view menus)
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CloseViewHandler extends AbstractHandler {
	private static final String PARAM_ID = "org.daisy.emerson.ui.views.close.viewID"; //$NON-NLS-1$
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		//we expect the command to have a param with the name 
		//of the part to minimize
		String viewID = event.getParameter(PARAM_ID); 
		if(viewID != null) {
			try {
				IWorkbenchPage page = 
					PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();				
				IViewReference view = page.findViewReference(viewID);				
				page.hideView(view);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
		return null;			 
	}
		
}
