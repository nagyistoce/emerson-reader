package org.daisy.emerson.ui.part;

import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

/**
 * Abstract parent of Views in the Emerson UI context.
 * <p>Note: clients must call {@link #init(Control)}</p>
 * @author Markus Gylling
 */
public abstract class EmersonViewPart extends ViewPart implements IPartListener2 {

	private IContextService contextService = null;
	private IContextActivation contextActivation = null;
	private String contextID;
	private String viewID;
	private Control control;	
		
	public EmersonViewPart(String viewID, String contextID) {
		this.viewID = viewID;
		this.contextID = contextID;
	}
	
	public boolean hasFocus() {
		return contextActivation != null;
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}
	
	@Override
	public void setFocus() {
		if(this.control!=null&&!this.control.isDisposed()) {
			this.control.setFocus();
		}		
	}
	
	private IContextService getContextService() {
		if(this.contextService==null) {
			this.contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
		}
		return this.contextService;
	}
	
	/**
	 * Initialize this EmersonViewPart.
	 * Subclasses must call this method explicitly.
	 * @param control The views main control
	 */
	public void init(Control control) {
		this.control = control;
		getViewSite().getPage().addPartListener(this);
		
		this.control.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				if(e.childID == ACC.CHILDID_SELF) {
					e.result = getTitle();
				}					
			}							
		});
	}

	private void activate() {
		contextActivation = getContextService().activateContext(contextID);		
	}
	
	private void deactivate() {
		if(contextActivation != null) {
			//System.err.println("deactivating context " + contextID);
			getContextService().deactivateContext(contextActivation);
			contextActivation = null;
		}
	}	
	
	@Override
	public void dispose() {
		getViewSite().getPage().removePartListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partActivated(IWorkbenchPartReference partRef) {
		if(partRef.getId().equals(this.viewID)) {
			activate();
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partDeactivated(IWorkbenchPartReference partRef) {
		if(partRef.getId().equals(this.viewID)) {
			deactivate();
		}			
	}
	
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
				
	}

	public void partClosed(IWorkbenchPartReference partRef) {
				
	}

	public void partHidden(IWorkbenchPartReference partRef) {
				
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
				
	}

	public void partOpened(IWorkbenchPartReference partRef) {
				
	}

	public void partVisible(IWorkbenchPartReference partRef) {
			
	}

}
