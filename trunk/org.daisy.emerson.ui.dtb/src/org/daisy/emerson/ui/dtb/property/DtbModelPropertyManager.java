package org.daisy.emerson.ui.dtb.property;

import org.daisy.emerson.ui.dtb.preferences.PreferenceConstants;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.dtb.Activator;
import org.daisy.reader.model.dtb.DtbModel;
import org.daisy.reader.model.property.IModelPropertyManager;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.smil.AudioClipCursor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

public class DtbModelPropertyManager implements IModelPropertyManager, IPropertyChangeListener {
	
	private IPreferenceStore store; 
	private DtbModel model;
	
	public DtbModelPropertyManager() {
		
	}

	public void dispose() {
		getStore().removePropertyChangeListener(this);
		this.model = null;
	}
	
	public void initialize(Model model) {
		this.model = (DtbModel)model;
		
		getStore().addPropertyChangeListener(this);
		
		//set initial skippability values
		
		AudioClipCursor cursor = this.model.getSpine().getAudioClipCursor();
		
		if(getStore().getBoolean(PreferenceConstants.P_SKIP_FOOTNOTES)){
			cursor.setSkippability(Semantic.NOTE, Boolean.TRUE);
		}
		if(getStore().getBoolean(PreferenceConstants.P_SKIP_PAGES)){
			cursor.setSkippability(Semantic.PAGE_NUMBER,Boolean.TRUE);
		}
		if(getStore().getBoolean(PreferenceConstants.P_SKIP_PRODNOTES)){
			cursor.setSkippability(Semantic.OPTIONAL_PRODUCER_NOTE,Boolean.TRUE);
		}
		if(getStore().getBoolean(PreferenceConstants.P_SKIP_SIDEBARS)){
			cursor.setSkippability(Semantic.OPTIONAL_SIDEBAR,Boolean.TRUE);
		}				
		
	}

	private IPreferenceStore getStore() {
		if(store==null) {
			store = PlatformUI.getPreferenceStore();	
		}
		return store;
	}

	public void propertyChange(PropertyChangeEvent event) {
		
		if(this.model!=null && !this.model.isDisposed()) {
			String prop = event.getProperty();
			
	        if(prop!=null && prop.equals(PreferenceConstants.P_SKIP_FOOTNOTES)
	        		|| prop.equals(PreferenceConstants.P_SKIP_PAGES)
	        		|| prop.equals(PreferenceConstants.P_SKIP_PRODNOTES)
	        		|| prop.equals(PreferenceConstants.P_SKIP_SIDEBARS)
	        		) {
	        	
	        	try{	  	        			        		
	        		AudioClipCursor cursor = this.model.getSpine().getAudioClipCursor();
	        		Semantic sem = null;
	        		if(prop.equals(PreferenceConstants.P_SKIP_FOOTNOTES)) {
	        			sem = Semantic.NOTE;
	        		} else if(prop.equals(PreferenceConstants.P_SKIP_PAGES)) {
	        			sem = Semantic.PAGE_NUMBER;
	        		} else if(prop.equals(PreferenceConstants.P_SKIP_PRODNOTES)) {
	        			sem = Semantic.OPTIONAL_PRODUCER_NOTE;
	        		} else if(prop.equals(PreferenceConstants.P_SKIP_SIDEBARS)) {
	        			sem = Semantic.OPTIONAL_SIDEBAR;
	        		}
	        		cursor.setSkippability(sem, (Boolean)event.getNewValue());	        		
	        	}catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);					
				}
	        }
		}
	}
}
