package org.daisy.emerson.ui.notes.handlers;

import java.util.Iterator;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.notes.Note;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DereferenceHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection!=null && selection instanceof StructuredSelection) {
			StructuredSelection sel = (StructuredSelection)selection;
			for (Iterator<?> iterator = sel.iterator(); iterator.hasNext();) {
				Note m = (Note) iterator.next();					
				Model model = ModelManager.getModel();	
				if(model!=null && !model.isDisposed()) {
					IPosition pos = m.getLocation();
					if(pos!=null)
						model.setPosition(pos);
				}	
				break;
			}			
		}	
		return null;
	}
}
