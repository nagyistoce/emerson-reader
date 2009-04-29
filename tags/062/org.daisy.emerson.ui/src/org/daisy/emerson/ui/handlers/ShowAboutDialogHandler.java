package org.daisy.emerson.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Show the about dialog.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowAboutDialogHandler extends AbstractHandler {

	public ShowAboutDialogHandler() {
		
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ActionFactory.ABOUT.create(window).run();
		HandlerUtil.getCurrentSelection(event);
		return null;
	}
	
}
