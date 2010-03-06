package org.daisy.emerson.ui;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
/*
 *  ** hi prio **
 *  TODO when browser focus, sometimes keys stop working
 *  TODO Orca testing
 *  TODO deb for linux installer
 *  TODO case sensitivity on linux, if url to smil or textcontent or audio doesnt resolve, try to do a case check
 *  TODO throw contentnotsupported if 2.02 mvdtb
 *  TODO togglable text highlight in browser
 *  TODO local DTDs instead of null doctype
 *  TODO implement SubstituteProvider for epub as well, enables lazy provision
  
 * ** major next stuff **
 * TODO add mozilla+xulrunner support, MozillaPosition (must be Autonomous)
 * TODO add lucene support (auto wordindices, bookshelf search)
 * 
 * ** lower prio **
 * TODO audio devices closing properly?
 * TODO add img popup support (smil:img)
 * TODO daisy, add large font browser w current sync only
 *  
 * TODO accelconfig for dtb stuff so that those keys arent active when epub
 * TODO could have a search harddrive for books wizard
 * 
 * ** lowest prio **
 * TODO library clean nonexisting wizard (w progressbar)
 * 
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
		
}
