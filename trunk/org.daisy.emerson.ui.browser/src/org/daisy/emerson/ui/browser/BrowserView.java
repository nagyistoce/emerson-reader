package org.daisy.emerson.ui.browser;

import java.net.URL;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.part.EmersonViewPart;
import org.daisy.emerson.ui.preferences.PreferenceConstants;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.adapt.ITextURLAdapter;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.daisy.reader.util.URIStringParser;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public class BrowserView extends EmersonViewPart implements IModelStateChangeListener, 
	IPositionChangeListener, IPropertyChangeListener {

	public static final String VIEW_ID = "org.daisy.emerson.ui.views.browser";	 //$NON-NLS-1$
	public static final String CONTEXT_ID = "org.daisy.emerson.contexts.views.browser"; //$NON-NLS-1$
	private String lastSetURL;	
	private Browser browser;	
	private final IPreferenceStore preferenceStore;
	private boolean shouldForceFocus = true;
	//private nsIWebBrowser mozilla = null;

	public BrowserView() {
		super(VIEW_ID,CONTEXT_ID);
		preferenceStore = Activator.getDefault().getPreferenceStore();		
		preferenceStore.addPropertyChangeListener(this);
		shouldForceFocus = preferenceStore.getBoolean(PreferenceConstants.P_FORCE_FOCUS_CHANGE);
	}
	
	@Override
	public void setFocus() {				
		if (browser.forceFocus()) {
			// an additional TAB is required to focus the browser content:
			browser.traverse(SWT.TRAVERSE_TAB_NEXT);
		}else{
			super.setFocus();	
		}					
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {		
		browser = createBrowser(parent);
		init(browser);
		reset();		
	}
			
	private void reset() {
		if(!browser.isDisposed())
			browser.setText(getBackgroundDocument());		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site);
        ModelManager.addStateChangeListener(this);
        ModelManager.addPositionChangeListener(this);
    }
      
    /*
     * (non-Javadoc)
     * @see org.daisy.reader.model.state.IModelStateChangeListener#modelStateChanged(org.daisy.reader.model.state.ModelStateChangeEvent)
     */
	public void modelStateChanged(ModelStateChangeEvent event) {
		
		if(event.getNewState()==ModelState.DISPOSING) {
			reset();
		}else if(event.getNewState()==ModelState.LOADED) {
			//TODO dont do anything, wait for positionchangeevent
			try{
				//load the first doc in the spine
				Object first = ModelManager.getModel().getSpine().get(0);	
				if(first!=null) {
					ITextURLAdapter adapter = (ITextURLAdapter)
						Platform.getAdapterManager().getAdapter(first, ITextURLAdapter.class);
					if(adapter!=null) {
						final URL u = adapter.getURL(first);						
						setURL(u);	
					}
				}
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}
		}
	}
		
	String previousFragment = null;
	
	public boolean setURL(URL url) {
		if(url!=null && !browser.isDisposed()) {

			String target = url.toExternalForm();
			if(!target.equals(lastSetURL)) {
				if(browser.setUrl(target)) {
					
					String fragment = URIStringParser.getFragment(target);
					
					if(previousFragment!=null) {
						//TODO store original bgcolor
						browser.execute("document.getElementById('"  //$NON-NLS-1$
								+ previousFragment + "').style.backgroundColor='rgb(255,255,255)';"); //$NON-NLS-1$
					}
					
					browser.execute("document.getElementById('"  //$NON-NLS-1$
							+ fragment + "').style.backgroundColor='rgb(230,230,230)';"); //$NON-NLS-1$
					
					previousFragment = fragment;

					//use scrollablecomposite if shaky	
					//browser.execute("window.scrollBy(0,-20)");	//$NON-NLS-1$
					
					if(shouldForceFocus) setFocus(); 
					
					lastSetURL = target;
					
					return true;
				}	
			}
		}	
		return false;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.IPositionChangeListener#positionChanged(org.daisy.reader.model.position.ModelPositionChangeEvent)
	 */
	public void positionChanged(ModelPositionChangeEvent event) {
					
		ITextURLAdapter adapter = (ITextURLAdapter)
			Platform.getAdapterManager().getAdapter(
					event.getNewPosition(), ITextURLAdapter.class);	
		
		if(adapter!=null) {
			final URL u = adapter.getURL(event.getNewPosition());						
			setURL(u);	
		}
	}
            
    @Override
    public void dispose() {    	
        ModelManager.removeStateChangeListener(this);
        ModelManager.removePositionChangeListener(this);
    	super.dispose();
    }
	
	private Browser createBrowser(Composite parent) {		
//		GridLayout gridLayout = new GridLayout();		
//		parent.setLayout(gridLayout);	
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		//Device.DEBUG = true;
		//nsIWebBrowser mozilla;
		try{
			browser = new Browser(parent, SWT.NONE);
			//browser = new Browser(parent, SWT.NONE);
			//Mozilla.getInstance().initialize(new File("/usr/lib/xulrunner-1.9.0.5/"));
			//mozilla = (nsIWebBrowser)browser.getWebBrowser();
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			browser = null;
		}	
		
		if(browser==null) {
			browser = new Browser(parent, SWT.NONE);
		}	

								
		browser.setLayoutData(data);
										
		browser.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

			}
			
			@Override
			public void keyReleased(KeyEvent e) {		
				//System.err.println("browser keyReleased event"); //$NON-NLS-1$
				if(e.keyCode == 65536) { //alt  
					/*
					 * Browser hidden menu seems to catch this, 
					 * need to pass alt to main menu, for Win
					 * TODO equivalent on other OS's?
					 */
					browser.getParent().forceFocus();				
				}
			}	
		});
		
//		browser.addMouseListener(new MouseAdapter() {
//			public void mouseDown(MouseEvent e) {
//				//System.err.println("mousedown");				
//			}
//
//			public void mouseUp(MouseEvent e) {
//				//System.err.println("mouseup");
//				//getFocusNS();									
//			}
//		});
		
//		browser.getAccessible().addAccessibleControlListener(new AccessibleControlListener(){
//
//			public void getChild(AccessibleControlEvent arg0) {
//				System.err.println("AccessibleControlListener#getChild");
//				
//			}
//
//			public void getChildAtPoint(AccessibleControlEvent arg0) {
//				System.err.println("AccessibleControlListener#getChildAtPoint");
//				
//			}
//
//			public void getChildCount(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getChildCount");
//				
//			}
//
//			public void getChildren(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getChildren");
//			}
//
//			public void getDefaultAction(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getDefaultAction");	
//			}
//
//			public void getFocus(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getFocus");				
//			}
//
//			public void getLocation(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getLocation");
//			}
//
//			public void getRole(AccessibleControlEvent e) {
//				
//				System.err.println("AccessibleControlListener#getRole");	
//				//e.detail = ACC.ROLE_WINDOW;
//			}
//
//			public void getSelection(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getSelection");	
//			}
//
//			public void getState(AccessibleControlEvent e) {
//				
//				System.err.println("AccessibleControlListener#getState");			
//				e.detail = ACC.STATE_FOCUSABLE | ACC.STATE_NORMAL;
//			}
//
//			public void getValue(AccessibleControlEvent arg0) {
//				
//				System.err.println("AccessibleControlListener#getValue");
//			}});
		
//		browser.getAccessible().addAccessibleListener(new AccessibleListener(){
//
//			public void getDescription(AccessibleEvent arg0) {
//				
//				System.err.println("AccessibleListener#getDescription");				
//			}
//
//			public void getHelp(AccessibleEvent arg0) {
//				
//				System.err.println("AccessibleListener#getHelp");
//			}
//
//			public void getKeyboardShortcut(AccessibleEvent arg0) {
//				
//				System.err.println("AccessibleListener#getKeyboardShortcut");
//			}
//
//			public void getName(AccessibleEvent arg0) {
//				
//				System.err.println("AccessibleListener#getName");
//			}});
		
//			browser.getAccessible().addAccessibleTextListener(new AccessibleTextListener(){
//
//				public void getCaretOffset(AccessibleTextEvent arg0) {
//					
//					System.err.println("T");
//				}
//
//				public void getSelectionRange(AccessibleTextEvent arg0) {
//					
//					System.err.println("T");
//				}});
		
		return browser;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(PreferenceConstants.P_FORCE_FOCUS_CHANGE)) {
			shouldForceFocus = (Boolean)event.getNewValue();
		}
	}
	
//	private void getFocusNS() {
//		try{
//			System.err.println(mozilla.getContentDOMWindow().getSelection().getFocusNode().getParentNode().getLocalName());
//		}catch (Throwable t) {
//			t.printStackTrace();
//		}
//		browser.execute("var targ;if (!e){var e=window.event;}if(e.target){targ=e.target;}else if (e.srcElement){targ=e.srcElement;}if (targ.nodeType==3){targ = targ.parentNode;}var tname;tname=targ.tagName;alert('You clicked on a ' + tname + ' element. ID=' + targ.getAttribute('id'));");
//
//	}
		
	
	
	private String getBackgroundDocument(){
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Emerson</title><style type='text/css'>body{background-color:"); //$NON-NLS-1$
		sb.append(getBackgroundColor());
		sb.append("; font-size:100.01%;}p.a{color:"); //$NON-NLS-1$
		sb.append(getForegroundColor());
		sb.append("; font-size:10em; align:center;}</style></head><body><p class='a' align='center'>emerson</p></body><html>"); //$NON-NLS-1$
		return sb.toString();
	}
	
	private String getBackgroundColor() {
		try{
		  Color bc = browser.getParent().getBackground();
		  return "rgb("+ bc.getRed() +"," + bc.getGreen() + "," + bc.getBlue() + ")";    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
		}catch (Exception e) {}
		return "rgb(230,230,230)"; //$NON-NLS-1$
	}

	private String getForegroundColor() {
		try{
			  Color bc = browser.getParent().getBackground();
			  return "rgb("+ bc.getRed()+10 +"," + bc.getGreen()+10 + "," + bc.getBlue()+10 + ")";    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
		}catch (Exception e) {}
		return "rgb(240,240,240)"; //$NON-NLS-1$
	}

	
	

}
