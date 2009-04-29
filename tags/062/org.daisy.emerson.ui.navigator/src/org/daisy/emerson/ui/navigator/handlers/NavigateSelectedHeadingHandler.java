package org.daisy.emerson.ui.navigator.handlers;

import java.net.URL;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.model.position.URIPosition;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Navigate to selected heading.
 * @author Markus Gylling
 */
public class NavigateSelectedHeadingHandler extends NavigateSelectedHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);		
		if(selection instanceof StructuredSelection) {
			//o is a member of the model that backs the view
			Object o = ((StructuredSelection) selection).getFirstElement();	
			INavigationItem item = (INavigationItem)o;
			String target = item.getTarget();
			Model model = ModelManager.getModel();
			if(target!= null && model!=null && !isDisposed(model)) {
				URL source = model.getNavigation().getSourceURL();				
				navigateTo(new URIPosition(target,source));	
			}			
		}					
		return null;		
	}
}
