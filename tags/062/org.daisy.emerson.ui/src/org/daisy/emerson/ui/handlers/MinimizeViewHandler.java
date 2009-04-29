//package org.daisy.emerson.ui.handlers;
//
//import org.eclipse.core.commands.AbstractHandler;
//import org.eclipse.core.commands.ExecutionEvent;
//import org.eclipse.core.commands.ExecutionException;
//import org.eclipse.core.commands.IParameter;
//import org.eclipse.core.commands.common.NotDefinedException;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchPartReference;
//import org.eclipse.ui.PlatformUI;
//
///**
// * Minimize a view. This is a separate command+handler for accessibility reasons (need to repeat in main and view menus)
// * @see org.eclipse.core.commands.IHandler
// * @see org.eclipse.core.commands.AbstractHandler
// */
//public class MinimizeViewHandler extends AbstractHandler {
//
//	public MinimizeViewHandler() {
//		
//	}
//
//	public Object execute(ExecutionEvent event) throws ExecutionException {		
//		//we expect the command to have a param with the name of the part to minimize
//		 
//		try {
//			IParameter[] params = event.getCommand().getParameters();
//			for (int i = 0; i < params.length; i++) {
//				IParameter param = params[i];
//				if(param.getName().startsWith("org.daisy.emerson.views")) {
//					IWorkbenchPage page = 
//						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//					IWorkbenchPartReference view = page.findViewReference(param.getName());
//						page.setPartState(view, IWorkbenchPage.STATE_MINIMIZED);
//				}
//			}
//		} catch (NotDefinedException e) {
//			e.printStackTrace();
//		}
//		
//
//		return null;			 
//	}
//		
//}
