package org.daisy.emerson.ui.handlers;

import org.daisy.reader.model.ModelManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Close the currently open publication.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ClosePublicationHandler extends AbstractHandler {

	public ClosePublicationHandler() {
		
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {	
		ModelManager.unload();
		return null;
	}
}
