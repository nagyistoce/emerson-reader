package org.daisy.emerson.ui.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class DebugOpenHandler extends OpenPublicationHandler {

	
	@Override
	protected URL getInput(IWorkbenchWindow window, ExecutionEvent event) throws MalformedURLException, NotDefinedException, ParameterValuesException, ExecutionException {
		String debugFile = System.getProperty("emerson.debug.open"); //$NON-NLS-1$
		return toURI(debugFile).toURL();		
	}

	@Override
	protected IWorkbenchWindow getWorkbenchWindow(ExecutionEvent event) throws ExecutionException {
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0];
	}
	
}
