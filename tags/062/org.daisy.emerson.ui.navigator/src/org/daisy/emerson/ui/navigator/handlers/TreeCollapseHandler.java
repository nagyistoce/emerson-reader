package org.daisy.emerson.ui.navigator.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeViewer;
import org.daisy.emerson.ui.navigator.NavigatorView;

public class TreeCollapseHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		TreeViewer viewer = NavigatorView.getViewer();		
		if(viewer!=null && ! viewer.getControl().isDisposed()) {
			viewer.collapseAll();
		}
		return null;
	}
}
