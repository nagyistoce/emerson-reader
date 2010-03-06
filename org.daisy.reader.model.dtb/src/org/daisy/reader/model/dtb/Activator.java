package org.daisy.reader.model.dtb;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.daisy.reader.model.dtb"; //$NON-NLS-1$

	private static Activator plugin;
	
	public Activator() {

	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public void logError(String message, Throwable exception) {	
		getLog().log(new Status(Status.ERROR,PLUGIN_ID,message,exception));
	}

	public void log(String message) {	
		getLog().log(new Status(Status.INFO,PLUGIN_ID,message));
	}
	
}
