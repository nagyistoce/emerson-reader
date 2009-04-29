package org.daisy.emerson.ui.notes;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.daisy.emerson.ui.notes"; //$NON-NLS-1$
	private static Activator plugin;
	private static Map<String,Image> imageCache = null; 
	
	public Activator() {
	
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		imageCache = new HashMap<String,Image>();
	}

	public void stop(BundleContext context) throws Exception {
		imageCache.clear(); imageCache=null;
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public void logError(String message, Throwable exception) {
		getLog().log(new Status(Status.ERROR,PLUGIN_ID,message,exception));
	}
	
	public static Image getImage(String path) {
		if(!imageCache.containsKey(path)) {
			ImageDescriptor id = imageDescriptorFromPlugin(PLUGIN_ID, path);
			Image i = id.createImage(false);
			imageCache.put(path, i);
		}
		return imageCache.get(path);		
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
