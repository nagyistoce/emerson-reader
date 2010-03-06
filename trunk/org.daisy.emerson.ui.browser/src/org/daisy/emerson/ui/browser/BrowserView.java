package org.daisy.emerson.ui.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate;
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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ListenerList;
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
import org.osgi.framework.Bundle;

public class BrowserView extends EmersonViewPart implements IModelStateChangeListener, 
	IPositionChangeListener, IPropertyChangeListener {

	public static final String VIEW_ID = "org.daisy.emerson.ui.views.browser";	 //$NON-NLS-1$
	public static final String CONTEXT_ID = "org.daisy.emerson.contexts.views.browser"; //$NON-NLS-1$
	public static final String BROWSER_BHV_DELEGATE_EP_ID = "org.daisy.emerson.ui.browser.behaviorDelegate"; //$NON-NLS-1$														
	public Browser browser;	
	private final IPreferenceStore preferenceStore;
	private final ListenerList targetChangeListeners;
	private final List<IBrowserBehaviorDelegate> behaviorDelegates;
	
	/**
	 * The currently loaded URL -- the last URL that was successfully loaded in the browser, including fragment
	 */
	private String currentURL;
	
	/**
	 * The currently active fragment in the currently loaded document
	 */
	String currentFragment = null;
	
	/**
	 * The currently loaded document, -- currentURL minus the fragment
	 */
	String currentDocument = null;
		
	/**
	 * Whether the browser control should recieve keyboard focus each time the
	 * currently active URL changes 
	 */
	private boolean shouldForceFocus = true;
	

	public BrowserView() {
		super(VIEW_ID,CONTEXT_ID);
		preferenceStore = Activator.getDefault().getPreferenceStore();		
		preferenceStore.addPropertyChangeListener(this);
		shouldForceFocus = preferenceStore.getBoolean(PreferenceConstants.P_FORCE_FOCUS_CHANGE);
		targetChangeListeners = new ListenerList(ListenerList.IDENTITY);
		behaviorDelegates = new ArrayList<IBrowserBehaviorDelegate>(5);
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
		disposeBehaviorDelegates();
		if(!browser.isDisposed()) {
			browser.setJavascriptEnabled(true);
			browser.setText(getBackgroundDocument());				
		}			
		currentURL = null;		
		currentDocument = null;
		currentFragment = null;
	}

	/**
	 * Dispose any IBrowserBehaviorDelegate providers and clear the services list. 
	 * <p>This method is invoked every time a ModelState change event DISPOSING occurs, 
	 * and when this View itself is disposed.</p>
	 */
	private void disposeBehaviorDelegates() {
		for(IBrowserBehaviorDelegate service : behaviorDelegates) {
			if(!service.isDisposed()) service.dispose();
		}
		behaviorDelegates.clear();
	}

	/**
	 * Load any IBrowserBehaviorDelegate providers. 
	 * <p>This method is invoked every time a ModelState change event LOADED occurs,
	 * so that services can be loaded depending on the model type.</p>
	 */
	private void loadBehaviorDelegates() {		
				
		IExtensionRegistry registry = Platform.getExtensionRegistry();						
		IExtensionPoint ep = registry.getExtensionPoint(BROWSER_BHV_DELEGATE_EP_ID);    		    				
		if (ep!=null) {
			IExtension[] exts = ep.getExtensions();				
			for (int i = 0; i < exts.length; i++) {
				IExtension ext = exts[i];
				Bundle bundle = Platform.getBundle(ext.getContributor().getName());
				IConfigurationElement[] elems = ext.getConfigurationElements();
				for (int j = 0; j < elems.length; j++) {
					IConfigurationElement elem = elems[j];
					if(elem.getName().equals("provider")) { //$NON-NLS-1$
						String clazz = elem.getAttribute("class");
						try {							
							IBrowserBehaviorDelegate ibs = (IBrowserBehaviorDelegate) 
								bundle.loadClass(clazz).newInstance();
							ibs.initialize(this);
							if(ibs!=null) {								
								behaviorDelegates.add(ibs);
							}
						} catch (Exception e) {
							Activator.getDefault().logError(e.getMessage(), e);
						}
					}
				}
			}
		}	
	}
	
	
	@Override
	public void dispose() {    	
	    ModelManager.removeStateChangeListener(this);
	    ModelManager.removePositionChangeListener(this);
	    disposeBehaviorDelegates();
	    browser.dispose();
		super.dispose();
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
			loadBehaviorDelegates();
			try{
				//load the first doc in the spine
				//TODO dont do this, wait for positionchange
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(PreferenceConstants.P_FORCE_FOCUS_CHANGE)) {
			shouldForceFocus = (Boolean)event.getNewValue();
		}
	}

	public boolean setURL(URL url) {
		if(url==null || browser.isDisposed()) return false;
		
		//parse out info on the incoming target
		String target = url.toExternalForm();		
		String document = URIStringParser.stripFragment(target);
		String fragment = URIStringParser.getFragment(target);			
		boolean newDocument = !document.equals(currentDocument);
		boolean newFragment = !fragment.equals(currentFragment);
		
		//if the incoming target is the same as previous, do nothing
		if(!newDocument && !newFragment) return true;
		
		//notify listeners of pending target change
		firePreTargetURLChangedEvent(document, currentDocument, fragment, currentFragment);		
		
		//set the browser to load new target
		if(browser.setUrl(target)) {

			//notify listeners that target changed
			firePostTargetURLChangedEvent(document, currentDocument, fragment, currentFragment);
			
			//update our local target state information
			currentFragment = fragment;
			currentDocument = document;
			currentURL = target;
			
			//set focus to browser view
			if(shouldForceFocus) setFocus();
			
			return true;
		}else{ //if(browser.setUrl(target))
			//handle an erronous URL:
			//check if we have left the previously successfully loaded URL
			if(!browser.getUrl().equals(currentURL)) {
				//try to reload previous
				if(!browser.setUrl(currentURL)) {
					//load the fallback
					browser.setText(getErrorDocument());
				}	
			}
			return false;
		} //if(browser.setUrl(target))
		
	}
	
	private void firePreTargetURLChangedEvent(String newDoc, String prevDoc, String newFragment, String prevFragment) {
		Object[] listening = targetChangeListeners.getListeners();
		for (int i = 0; i < listening.length; ++i) {
			((IBrowserTargetChangeListener) 
					listening[i]).preTargetChange(newDoc, prevDoc, newFragment, prevFragment);
		}		
	}
	
	private void firePostTargetURLChangedEvent(String newDoc, String prevDoc, String newFragment, String prevFragment) {
		Object[] listening = targetChangeListeners.getListeners();
		for (int i = 0; i < listening.length; ++i) {
			((IBrowserTargetChangeListener) 
					listening[i]).postTargetChange(newDoc, prevDoc, newFragment, prevFragment);
		}		
	}

//	private void handleScroll(String fragment) {
//		//use scrollablecomposite if shaky	
//		execute("window.scrollBy(0,-20)");	//$NON-NLS-1$		
//	}

	@Override
	public void setFocus() {				
		if (browser.forceFocus()) {
			// an additional TAB is required to focus the browser content:
			browser.traverse(SWT.TRAVERSE_TAB_NEXT);
		}else{
			super.setFocus();	
		}					
	}

	private Browser createBrowser(Composite parent) {		
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		try{
			browser = new Browser(parent, SWT.NONE);
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			browser = null;
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
					 * TODO equivalent on other OS's
					 */
					browser.getParent().forceFocus();				
				}
			}	
		});
		
		return browser;
	}
	
	/**
	 * Execute javascript on the browser. This method only executes
	 * if the load progress is reported as complete.
	 */
	public boolean execute(String script) {
		//if(loadComplete) {
			try{
				return browser.execute(script);
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}	
		//}
		return false;
	}

	/**
	 * Evaluate javascript on the browser. This method only executes
	 * if the load progress is reported as complete.
	 */
	public Object evaluate(String script) {
		//if(loadComplete) {
			try{
				return browser.evaluate(script);
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}	
		//}
		return null;
	}

	/**
	 * Add the given listener to the list of objects that will be notified each
	 * time the browser in this BrowserView changes its current target URL.
	 */
	public void addTargetChangeListener(IBrowserTargetChangeListener listener){
		targetChangeListeners.add(listener);
	}

	/**
	 * Remove the given listener to the list of objects that will be notified each
	 * time the browser in this BrowserView changes its current target URL.
	 */
	public void removeTargetChangeListener(IBrowserTargetChangeListener listener){
		targetChangeListeners.remove(listener);
	}

	private String getBackgroundDocument(){
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Emerson</title><style type='text/css'>body{background-color:"); //$NON-NLS-1$
		sb.append(getBackgroundColor());
		sb.append("; font-size:100.01%;}p.a{color:"); //$NON-NLS-1$
		sb.append(getForegroundColor());
		sb.append("; font-size:10em; align:center; text-shadow: 0 2px 4px gray;}</style></head><body><p class='a' align='center'>emerson</p></body><html>"); //$NON-NLS-1$
		return sb.toString();
	}
	
	private String getErrorDocument(){
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Emerson</title><style type='text/css'>body{background-color:"); //$NON-NLS-1$
		sb.append(getBackgroundColor());
		sb.append("; font-size:100.01%;}p.a{color:"); //$NON-NLS-1$
		sb.append(getForegroundColor());
		sb.append("; font-size:10em; align:center; text-shadow: 0 2px 4px black;}</style></head><body><p class='a' align='center'>404</p></body><html>"); //$NON-NLS-1$
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
			return "rgb("+ (bc.getRed()+10) +"," + (bc.getGreen()+10) + "," + (bc.getBlue()+10) + ")";    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
		}catch (Exception e) {}
		return "rgb(240,240,240)"; //$NON-NLS-1$
	}

}