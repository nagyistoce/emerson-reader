package org.daisy.emerson.ui.handlers;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.ICommandIDs;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Close the application.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * @author Markus Gylling
 */
public class ExitHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {				
				
		//close the open publication, if one is open
		try {
			Model model = ModelManager.getModel();
			if(model!=null && !model.isDisposed()) {
				IWorkbenchSite site = HandlerUtil.getActiveSiteChecked(event);
				IHandlerService handlerService = (IHandlerService)site.getService(IHandlerService.class);										
				handlerService.executeCommand(ICommandIDs.CLOSE_PUBLICATION,null);			
			}
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage(), t);
		}
			
		//close the app		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);						
		return window.getWorkbench().close();		
	}
}
