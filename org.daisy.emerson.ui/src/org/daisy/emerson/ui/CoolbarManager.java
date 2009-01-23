package org.daisy.emerson.ui;

import org.daisy.emerson.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 * Handle coolbar toggling based on preferencestore setting.
 * @author Markus Gylling
 */
class CoolbarManager implements IPropertyChangeListener {
	private final IPreferenceStore store;
	private static IWorkbenchWindowConfigurer windowConfigurer; 
	
	CoolbarManager(IWorkbenchWindowConfigurer configurer) {
		windowConfigurer = configurer;
		store = Activator.getDefault().getPreferenceStore();		
		store.addPropertyChangeListener(this);
		boolean visible = store.getBoolean(PreferenceConstants.P_SHOW_COOLBAR);
		update(visible);
	}
	
	void close() {
		if(store!=null)
			store.removePropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(PreferenceConstants.P_SHOW_COOLBAR))
			update((Boolean)event.getNewValue());		
	}
	
	private void update(Boolean shouldShow) {			
		try {
			windowConfigurer.setShowCoolBar(shouldShow);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().layout(true); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private boolean isVisible() {
//		boolean isVisible = false;
//		IEvaluationService service = (IEvaluationService) PlatformUI
//			.getWorkbench().getService(IEvaluationService.class);
//		IEvaluationContext appState = service.getCurrentState();
//		Object coolbar = appState.getVariable(
//				ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME);
//		if (coolbar instanceof Boolean) {
//			isVisible = ((Boolean) coolbar).booleanValue();
//		}		
//		return isVisible;
//	}
	
	
}
