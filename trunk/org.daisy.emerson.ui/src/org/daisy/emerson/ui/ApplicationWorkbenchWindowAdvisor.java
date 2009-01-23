package org.daisy.emerson.ui;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static final String DAISY_PREF_PAGE_PREFIX ="org.daisy"; //$NON-NLS-1$
	private static CoolbarManager coolbarManager;
	private static IWorkbenchWindowConfigurer windowConfigurer;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        windowConfigurer = configurer;
        //TODO for some reason, cant get extension+ini presentationFactory to work
        configurer.setPresentationFactory(new AccessiblePresentationFactory());
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(false);     
        

        //remove unwanted preference pages
		PreferenceManager preferenceManager = 
			PlatformUI.getWorkbench().getPreferenceManager();
		
		List<?> list  = preferenceManager.getElements(PreferenceManager.PRE_ORDER);
		for(Object o : list) {
			IPreferenceNode node = (IPreferenceNode)o;
			//System.err.println("preferenceNode: " + node.getId());
			if(!node.getId().startsWith(DAISY_PREF_PAGE_PREFIX)) { 
				preferenceManager.remove(node);
			}
		}
		
    }
            
    @Override
    public void postWindowClose() {
    	super.postWindowClose();
    	coolbarManager.close();
    }
    
    @Override
    public void postWindowOpen() {
    	super.postWindowOpen();
    
    	//add coolbar toogle visible support		
		coolbarManager = new CoolbarManager(windowConfigurer);
    	
//    	System.err.println("ApplicationWorkbenchWindowAdvisor#postWindowOpen auto load");  
//    	
//		IHandlerService hs = (IHandlerService)PlatformUI.getWorkbench().getService(IHandlerService.class);
//		ICommandService cs = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
//		
//		String[] files = new String[]{
//				
//				//"/home/markusg/dtbs/hauy.202.rev4/ncc.html"
//				"/home/markusg/workspace_pipeline/org.daisy.pipeline/samples/input/epub/valentin_hauy.epub"				
////				"/home/markusg/dtbs/christianity/d202-dtb/ncc.html"
////				"/home/markusg/Documents/numeric_dtb/daisy202/ncc.html"				
////				,"/home/markusg/workspace_pipeline/org.daisy.pipeline/samples/input/epub/wasteland.epub"
////				,"/home/markusg/Documents/numeric_dtb/z3986-mp3/speechgen.opf"
////				,"/media/t60_travelstar/dtbs/d202/christianity/d202-dtb/ncc.html"
////				"/home/markusg/Documents/hauy.202.rev4.short/ncc.html"
//		};
//				
//		try{
//			for (int i = 0; i < files.length; i++) {							
//				Command c = cs.getCommand("org.daisy.emerson.commands.debugload");
//				System.setProperty("emerson.debug.open",files[i]);				
//				Event e = new Event();
//				ExecutionEvent ee = hs.createExecutionEvent(c, e);						
//				c.executeWithChecks(ee);			
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.clearProperty("emerson.debug.open");
    }    
}
