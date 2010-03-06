package org.daisy.emerson.ui.handlers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
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
	
	private static final String LAST_OPEN_PATH = "emerson-lastOpenedPath"; //$NON-NLS-1$

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
		setFilterPath(dialog);			
		String sel = dialog.open();
		/*
		 * Store the last accessed path in the preference store.
		 * #setFilterPath will make use of it if set
		 */		
		String lastPath = sel.substring(0,sel.lastIndexOf(File.separator)+1);
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(LAST_OPEN_PATH, lastPath);		
		
		if(sel!=null) {
			return toURI(sel).toURL();
		}	
		return null;
	}

	private void setFileFilters(FileDialog dialog) {
		try{
			FileFilters filters = new FileFilters();
			
			String[] extensionFilters = filters.getExtensionFilters();
			if(extensionFilters.length > 0) {
				dialog.setFilterExtensions(filters.getExtensionFilters());
			}	
			
			String[] filterNames = filters.getNiceNames();
			if(filterNames.length>0) {
				dialog.setFilterNames(filters.getNiceNames());
			}
			
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
		}
	}

	private void setFilterPath(FileDialog dialog) {
		String path = null;
		try{
			final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			if(store!=null) {
				//get the last accessed path, set in #getInput above
				path = store.getString(LAST_OPEN_PATH);
				//if not available, get the default open path as set in preferences
				if(path==null || path.equals("")) {		 //$NON-NLS-1$
					path = store.getString(PreferenceConstants.P_DEFAULT_OPEN_PATH);		
					if(path==null || path.length()==0) {
						path = store.getDefaultString(PreferenceConstants.P_DEFAULT_OPEN_PATH);
					}
				}
			}
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
		}
		
		if(path!=null && path.length()>0) {
			dialog.setFilterPath(path);	
		}	
	}
	
	private void  showOpenFailure(Throwable cause, IWorkbenchWindow window) {
		MessageDialog.openError(window.getShell(),Messages.OpenPublicationHandler_OpenFailed1,
				Messages.OpenPublicationHandler_OpenFailed2 + cause.getMessage());
	}
	
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
    
    class FileFilters {
    	private static final String modelProviderEPID = "org.daisy.reader.model.provider";    	 //$NON-NLS-1$
    	private final Map<String,String> extensionFilters;  //extension,nicename
    	private final Map<String,String> nameFilters;  		//fullname,nicename
    	
    	/**
    	 * Get all available ModelProviders,
    	 * from them get the contenttype id's,
    	 * and from those, get the filter names and extensions
    	 */
    	public FileFilters() {
    		IExtensionRegistry registry = Platform.getExtensionRegistry();						
    		IExtensionPoint ep = registry.getExtensionPoint(modelProviderEPID);    		    		
    		extensionFilters = new HashMap<String,String>();
    		nameFilters = new HashMap<String,String>();
    		
    		if (ep!=null) {
				IExtension[] exts = ep.getExtensions();				
				for (int i = 0; i < exts.length; i++) {
					IExtension ext = exts[i];					
					IConfigurationElement[] elems = ext.getConfigurationElements();
					for (int j = 0; j < elems.length; j++) {
						IConfigurationElement elem = elems[j];
						if(elem.getName().equals("provider")) { //$NON-NLS-1$
							IConfigurationElement[] children = elem.getChildren();
							for (int s = 0; s < children.length; s++) {
								IConfigurationElement child = children[s];
								if(child.getName().equals("contentType")) { //$NON-NLS-1$
									String contentTypeID = child.getAttribute("contentTypeId");	//$NON-NLS-1$							
									IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeID);
									if(contentType!=null) {
										String[] names = contentType.getFileSpecs(IContentType.FILE_NAME_SPEC);
										for (int k = 0; k < names.length; k++) {											
											nameFilters.put(names[k], contentType.getName());
										}										
										String[] extensions = contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
										for (int k = 0; k < extensions.length; k++) {
											extensionFilters.put(extensions[k], contentType.getName());
										}
									}									
								}
							}
						}
					}
				}
    		}    		
    	}

    	/**
    	 * Get a 2-length nicename array, where the first
    	 * is for emerson-supported content, and the second
    	 * for all files
    	 * @return
    	 */
    	String[] getNiceNames() {
    		return new String[]
    			{Messages.OpenPublicationHandler_EmersonContent, 
    				Messages.OpenPublicationHandler_AllFiles};
    	}
    	
    	/**
    	 * Get a 2-length extension array, where the first
    	 * entry is the semicolon-separated string of the 
    	 * filters found, and the second a wildcard.
    	 */
    	String[] getExtensionFilters() {
    		return new String[]{buildSemiColonString(extensionFilters.keySet(),nameFilters.keySet()), "*.*"}; //$NON-NLS-1$
    	}
    	
		private String buildSemiColonString(Collection<String> extensionFilters,Collection<String> nameFilters) {
			StringBuilder sb = new StringBuilder();
			for(String filter : nameFilters) {
				sb.append(filter);
				sb.append(';');
			}
			for(String filter : extensionFilters) {
				sb.append('*');
				sb.append('.');
				sb.append(filter);
				sb.append(';');
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}	
    			
    }
    
}
