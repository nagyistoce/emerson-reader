package org.daisy.emerson.ui.library.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.daisy.emerson.ui.handlers.OpenPublicationHandler;
import org.daisy.reader.history.HistoryEntry;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReadSelectedHandler extends OpenPublicationHandler {
		
	@Override
	protected URL getInput(IWorkbenchWindow window, ExecutionEvent event) throws MalformedURLException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection!=null && selection instanceof StructuredSelection) {
			if(!selection.isEmpty()) {
				final StructuredSelection sel = (StructuredSelection)selection;					
	        	HistoryEntry entry = (HistoryEntry)sel.getFirstElement();	        				
	        	return entry.getLastManifestLocation();	        				
			}
		}
		return null;
	}
}
