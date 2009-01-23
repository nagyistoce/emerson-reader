//package org.daisy.emerson.ui.handlers;
//
//import org.eclipse.core.commands.AbstractHandler;
//import org.eclipse.core.commands.ExecutionEvent;
//import org.eclipse.core.commands.ExecutionException;
//import org.eclipse.core.commands.IParameter;
//import org.eclipse.core.commands.IParameterValues;
//import org.eclipse.core.commands.ParameterValuesException;
//import org.eclipse.core.commands.common.NotDefinedException;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchPartReference;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
//
///**
// * Restored a minimized a view or show a view in placeholder state.
// * <p>This is a separate command+handler for accessibility reasons.</p>
// * <p>The extra command is also for setFocus purposes.</p>
// * @see org.eclipse.core.commands.IHandler
// * @see org.eclipse.core.commands.AbstractHandler
// */
//public class RestoreViewHandler extends AbstractHandler {
//
//	public Object execute(ExecutionEvent event) throws ExecutionException {		
//		//we expect the command to have a param with the name of the part
//		
//		try {
//			
//			IParameter param2 = event.getCommand().getParameter("org.daisy.emerson.commands.view.restore.viewID");
//			IParameterValues values = null;
//			try {
//				values = param2.getValues();
//			} catch (ParameterValuesException e1) {
//				e1.printStackTrace();
//			}
//			System.err.println(values.toString());
//			
//			IParameter[] params = event.getCommand().getParameters();
//			
//			for (int i = 0; i < params.length; i++) {
//				IParameter param = params[i];
//				if(param.getName().startsWith("org.daisy.emerson.views")) {					
//					IWorkbenchPage page = 
//						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//					IWorkbenchPartReference view = page.findViewReference(param.getName());
//					if(view!=null) {	
//						/*
//						 * A NPE bug, should be fixed in next release:
//						 * page.setPartState(view, IWorkbenchPage.STATE_RESTORED);
//						 */                
//						page.activate(view.getPart(true));	
//					}else{
//						//view is in placeholder state
//						try {
//							page.showView(param.getName());
//						} catch (PartInitException e) {
//							e.printStackTrace();
//						}
//					}
//					page.setPartState(view, IWorkbenchPage.STATE_RESTORED);
//				}
//			}
//		} catch (NotDefinedException e) {
//			e.printStackTrace();
//		}
//		
//		return null;			 
//	}
//		
//}
