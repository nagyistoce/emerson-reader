package org.daisy.emerson.ui.handlers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.Messages;
import org.daisy.emerson.ui.preferences.PreferenceConstants;
import org.daisy.reader.history.HistoryList;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.util.User;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open a new publication. This class uses an open dialog. Clients may subclass.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class OpenPublicationHandler extends AbstractHandler {

//	private static Set<String> fileNameFilters = null;
	
	public OpenPublicationHandler() {
		
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {				
		try{
			//long start = System.nanoTime();
			final IWorkbenchWindow window = getWorkbenchWindow(event);	
			final URL input = getInput(window, event);
									
	        if(input!=null) {	    
	        	BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {		
	        		public void run() {	        				
	        			try {    	 
	        				ModelManager.load(input);
	        				Model model = ModelManager.getModel();	
	        					        				
	        				IPosition prior = getPriorPosition(model);
	        				if(prior!=null && model.setPosition(prior)) return;
	        				model.render();
	        					        				
	                    }catch (Throwable t) {
	                    	showOpenFailure(t,window);
	        	        }	                    
	        		}

					private IPosition getPriorPosition(Model model) {
						try{
							return HistoryList.getInstance().get(
									(String)model.getProperty(IPropertyConstants.PUBLICATION_UUID), 
										User.getID(), 
											(PublicationType)model.getProperty(
												IPropertyConstants.PUBLICATION_TYPE))
													.getLastPosition();
							
						}catch (Exception e) {
							Activator.getDefault().logError(e.getLocalizedMessage(), e);
						}	
						return null;
					}				
	        	});	        	
	        } //if(selectedFile!=null)
	        //long end = System.nanoTime();
	        //long duration = end-start;
	        //System.err.println("Load took " + duration/1000000 + " millis");
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
		}
		return null;
	}
		
	protected IWorkbenchWindow getWorkbenchWindow(ExecutionEvent event) throws ExecutionException {
		return HandlerUtil.getActiveWorkbenchWindowChecked(event);
	}

	protected URL getInput(IWorkbenchWindow window, ExecutionEvent event) throws Exception {
		FileDialog dialog = new FileDialog(window.getShell());
		dialog.setText(Messages.OpenPublicationHandler_OpenBook);		
		setFileFilters(dialog);
		setDefaultPath(dialog);			
		String sel = dialog.open();
		if(sel!=null)
			return toURI(sel).toURL();
		return null;
	}

	private void setFileFilters(FileDialog dialog) {
		//TODO filters
//		FileFilters filters = new FileFilters();
//		dialog.setFilterNames(filters.getNiceNames());  
//		dialog.setFilterExtensions(filters.getFilters());
		
	}

	private void setDefaultPath(FileDialog dialog) {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String path = store.getString(PreferenceConstants.P_DEFAULT_OPEN_PATH);		
		if(path==null || path.length()==0) {
			path = store.getDefaultString(PreferenceConstants.P_DEFAULT_OPEN_PATH);
		}
		if(path!=null)
			dialog.setFilterPath(path);		
	}

	private void  showOpenFailure(Throwable cause, IWorkbenchWindow window) {
		MessageDialog.openError(window.getShell(),Messages.OpenPublicationHandler_OpenFailed1,
				Messages.OpenPublicationHandler_OpenFailed2 + cause.getMessage());
	}
	
//	private list<signature> getsignatures(string selectedfile) throws signaturedetectionexception, signaturenotfoundexception {
//		list<signature> list = new linkedlist<signature>();			
//		signaturedetector detector = new signaturedetector();		
//   		list = detector.detect(selectedfile);
//		return list;
//	}
//	
//	/**
//	 * Collect all fileNameFilter extensions found in the registry.
//	 */	
//	private String[] getFileFilter() {
//		String id = "org.daisy.emerson.filenamefilter";		
//		
//		if (fileNameFilters==null) {					
//			fileNameFilters = new HashSet<String>();	  
//			IExtensionRegistry registry = Platform.getExtensionRegistry();						
//			IExtensionPoint ep = registry.getExtensionPoint(id);
//			if(ep!=null) {
//				IExtension[] exts = ep.getExtensions();				
//				for (int i = 0; i < exts.length; i++) {
//					IExtension ext = exts[i];					
//					IConfigurationElement[] elems = ext.getConfigurationElements();
//					for (int j = 0; j < elems.length; j++) {
//						IConfigurationElement elem = elems[j];
//						if(elem.getName().equals("filter")) {
//							fileNameFilters.add(elem.getAttribute("value"));
//						}														
//					}
//				}
//			}
//		}		
//		if(fileNameFilters.isEmpty()) {
//			fileNameFilters.add("*.*");
//		}	
//		return fileNameFilters.toArray(new String[fileNameFilters.size()]);
//	}
//	
	private static Pattern schemePattern = Pattern.compile("[a-z]{2,}:.*"); //$NON-NLS-1$
    
    /**
     * Convert a filename or a file URI to a <code>File</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>File</code> object
     */
    protected static File toFile(String filenameOrFileURI) {
        try {
            if (hasScheme(filenameOrFileURI)) {
                try {                    
                    File f = new File(new URI(filenameOrFileURI));
                    return f;
                } catch (URISyntaxException e) {
                    e.printStackTrace();                   
                }
                return null;
            } 
            return new File(filenameOrFileURI);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Convert a filename or a file URI to a <code>URI</code>
     * object.
     * @param filenameOrFileURI a filename or a file URI
     * @return a <code>URI</code> object
     */
    protected static URI toURI(String filenameOrFileURI) {
        File file = toFile(filenameOrFileURI);
        return file==null?null:file.toURI();
    }
    
    /**
     * Checks if a path starts with  scheme identifier. If it
     * does, it is assumed to be a URI.
     * @param test the string to test.
     * @return <code>true</code> if the specified string starts with a scheme
     * identifier, <code>false</code> otherwise. 
     */
    private static boolean hasScheme(String test) {
        return schemePattern.matcher(test).matches();        
    }
    
//    class FileFilters {
//		private String id = "org.daisy.raymond.filenamefilter"; //$NON-NLS-1$
//		private Map<String,String> filters = null; //name,filter
//		
//		public FileFilters() {
//			filters = new HashMap<String,String>();
//			IExtensionRegistry registry = Platform.getExtensionRegistry();						
//			IExtensionPoint ep = registry.getExtensionPoint(id);
//			if(ep!=null) {
//				IExtension[] exts = ep.getExtensions();				
//				for (int i = 0; i < exts.length; i++) {
//					IExtension ext = exts[i];					
//					IConfigurationElement[] elems = ext.getConfigurationElements();
//					for (int j = 0; j < elems.length; j++) {
//						IConfigurationElement elem = elems[j];
//						if(elem.getName().equals("filter")) { //$NON-NLS-1$
//							filters.put(elem.getAttribute("name"),elem.getAttribute("value")); //$NON-NLS-1$ //$NON-NLS-2$
//						}														
//					}
//				}
//			}
//		}
//		
//		String[] getNiceNames() {
//			//make a semicolon separated list, return a 1-length array
//			String s = buildSemiColonString(filters.keySet());
//			return new String[]{s,Messages.getString("OpenContentHandler.AllFiles")};
//		}
//		
//		String[] getFilters() {
//			//make a semicolon separated list, return a 1-length array
//			String s = buildSemiColonString(filters.values());
//			return new String[]{s,"*.*"};			
//		}
//		
//		private String buildSemiColonString(Collection<String> values) {
//			StringBuilder sb = new StringBuilder();
//			for(String filter : values) {
//				sb.append(filter);
//				sb.append(';');
//			}
//			sb.deleteCharAt(sb.length()-1);
//			return sb.toString();
//		}
//
//	}

}
