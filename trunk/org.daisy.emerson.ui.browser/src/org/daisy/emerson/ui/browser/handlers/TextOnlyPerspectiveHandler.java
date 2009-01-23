package org.daisy.emerson.ui.browser.handlers;

import org.daisy.emerson.ui.browser.BrowserView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Close all open viewparts but the browser.
 * @author Markus Gylling
 */
public class TextOnlyPerspectiveHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try{
			IWorkbenchPage page 
				= PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			
			IViewReference[] views = page.getViewReferences();
			for (int i = 0; i < views.length; i++) {
				IViewReference view = (IViewReference) views[i];
				if(!view.getId().equals(BrowserView.VIEW_ID)) {
					//System.err.println("closing " + view.getId()); //$NON-NLS-1$
					page.hideView(view);
				}
			}
		}catch (Exception e) {
			
		}
		return null;
	}

}
