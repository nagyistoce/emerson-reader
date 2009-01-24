package org.daisy.emerson.ui.navigator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.daisy.emerson.ui.part.EmersonViewPart;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.adapt.ILabelAdapter;
import org.daisy.reader.model.adapt.ILazyTreeAdapter;
import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;

public class NavigatorView extends EmersonViewPart implements IDoubleClickListener, 
	IPositionChangeListener, IModelStateChangeListener {
	
	public static final String CONTEXT_ID = "org.daisy.emerson.contexts.views.navigator"; //$NON-NLS-1$
	public static final String VIEW_ID = "org.daisy.emerson.ui.views.navigator"; //$NON-NLS-1$
	private static TreeViewer treeViewer;
	private NavigatorContentProvider contentProvider = null;
	 
	public NavigatorView() {
		super(VIEW_ID,CONTEXT_ID);
	}
	 	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
        ModelManager.addStateChangeListener(this);
        ModelManager.addPositionChangeListener(this);
	}
	
/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, 
				SWT.VIRTUAL |SWT.BORDER |SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);	
		
		treeViewer.setUseHashlookup(true);
		
		contentProvider = new NavigatorContentProvider(); 
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new NavigatorLabelProvider());
		
		getSite().setSelectionProvider(treeViewer);
							
		treeViewer.addDoubleClickListener(this);
		
		if(ModelManager.getModel()!=null) {
			treeViewer.setInput(ModelManager.getModel().getNavigation());
			selectCurrent(ModelManager.getModel());
		}
		
		init(treeViewer.getControl()); 				
						
	}
	
	@Override
	public void dispose() {	
		ModelManager.removeStateChangeListener(this);    	
		ModelManager.removePositionChangeListener(this);
		treeViewer.getControl().dispose();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.state.IModelStateChangeListener#modelStateChanged(org.daisy.reader.model.state.ModelStateChangeEvent)
	 */
	public void modelStateChanged(final ModelStateChangeEvent event) {
		//System.err.println("NavigatorView#modelStateChanged");
		ModelState newState = event.getNewState();
		if(newState == ModelState.LOADED) {
			if(!treeViewer.getControl().isDisposed()) {
				treeViewer.setInput(
						event.getSource().getNavigation());		
				selectCurrent(ModelManager.getModel());
			}				
		}else if(newState == ModelState.DISPOSING) {
			treeViewer.setInput(new Object());			
		}		
	}
	
	public void positionChanged(ModelPositionChangeEvent event) {
		if(!treeViewer.getControl().isDisposed()) {
			Model model = ModelManager.getModel();
			if(model!=null && model.getCurrentState()!= ModelState.DISPOSING 
					&& model.getCurrentState()!= ModelState.DISPOSED) {
				selectCurrent(model);		
			}
		}
	}
	
	/**
	 * Select the current model heading in the tree viewer.
	 * @param model
	 */
	private INavigationItem lastSelected = null;
	private void selectCurrent(Model model) {
		INavigationItem item = model.getNavigation().getCurrent(Semantic.HEADING);
		
		if(item!=null && item!=lastSelected) {
			treeViewer.setSelection(new StructuredSelection(item), true);
			treeViewer.setExpandedState(item, true);
			lastSelected = item;
			//TODO if the viewer is closed and reopened, the current item is not selected
			//although we do pass through this method. The same is true at initial load.
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		final IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
		try {			
			handlerService.executeCommand(ICommandIDs.NAVIGATE_HEADING,null);
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage(), t);
		}		
	}
	
	public static TreeViewer getViewer() {
		return treeViewer;
	}

	class NavigatorContentProvider implements ILazyTreeContentProvider {
		Map<Class<?>, ILazyTreeAdapter> adapters = null;
		
		public NavigatorContentProvider() {
			adapters = new HashMap<Class<?>, ILazyTreeAdapter>();
		}
		public Object getParent(Object element) {
			ILazyTreeAdapter adapter = getAdapter(element);
			if(adapter!=null)
				return adapter.getParent(element);
			return null;
		}

		public void updateChildCount(Object element, int currentChildCount) {
			ILazyTreeAdapter adapter = getAdapter(element);
			if(adapter!=null) {
				int count = adapter.getChildCount(element);
				if(count!=currentChildCount)
					treeViewer.setChildCount(element, count);
			}
		}

		public void updateElement(Object parent, int index) {
			ILazyTreeAdapter adapter = getAdapter(parent);
			Object child = adapter.getChild(parent, index);
			treeViewer.replace(parent, index, child);
			updateChildCount(child, 0);			
		}
		
		private ILazyTreeAdapter getAdapter(Object element) {
			if(!adapters.containsKey(element.getClass())) {
				ILazyTreeAdapter adapter = (ILazyTreeAdapter)
					Platform.getAdapterManager().getAdapter(
							element, ILazyTreeAdapter.class);
				if(adapter!=null)
					adapters.put(element.getClass(), adapter);
			}
			return adapters.get(element.getClass());
		}
		


		public void dispose() {
			adapters.clear();			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
		
	}
			
	class NavigatorLabelProvider implements ILabelProvider {
		Set<ILabelProviderListener> listeners;
		Map<Class<?>, ILabelAdapter> adapters = null;
				
		public NavigatorLabelProvider() {
			listeners = new HashSet<ILabelProviderListener>();
			adapters = new HashMap<Class<?>, ILabelAdapter>();
		}
				
		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {			
			try{
				if(!adapters.containsKey(element.getClass())) {
					adapters.put(element.getClass(), (ILabelAdapter)Platform.getAdapterManager().getAdapter(element, ILabelAdapter.class));
				}				 
				return adapters.get(element.getClass()).getLabel(element);				
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}			
			return element.toString();
		}
		
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			listeners.remove(listener);			
		}
		
		public void addListener(ILabelProviderListener listener) {
			listeners.add(listener);			
		}

		public void dispose() {
			listeners.clear();
			adapters.clear();
		}

	}	
}