package org.daisy.emerson.ui.library.handlers;

import java.util.Iterator;

import org.daisy.reader.history.HistoryEntry;
import org.daisy.reader.history.HistoryList;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteSelectionHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection!=null && selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection)selection;
			for (Iterator<?> iterator = sel.iterator(); iterator.hasNext();) {
				HistoryEntry entry = (HistoryEntry)iterator.next();
				//if the entry is open, close first (else its added again when closed)
				Model model = ModelManager.getModel();				
				if(model!=null && !model.isDisposed() && entry.equals(model)) {
					ModelManager.unload();					
				}
				//remove from history model, which in its turn will notify the view
				HistoryList.getInstance().remove(entry);				
			}
		}	
		return null;
	}


}
