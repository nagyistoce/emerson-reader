package org.daisy.emerson.ui.browser;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.daisy.emerson.ui.browser"; //$NON-NLS-1$
	
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

}
