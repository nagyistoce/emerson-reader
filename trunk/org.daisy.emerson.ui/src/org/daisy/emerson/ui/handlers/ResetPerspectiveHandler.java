package org.daisy.emerson.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Reset perspective
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ResetPerspectiveHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {				
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if(window!=null)
			window.getActivePage().resetPerspective();		
		return null;
	}
}
