package org.daisy.reader.model.dtb.source;

import java.util.HashMap;
import java.util.Map;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.dtb.DtbModel;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Provide DTB Model-specific sources for Property testing.
 * @see source.PropertySourceProvider in org.daisy.reader.model
 * @author Markus Gylling
 */

public class PropertySourceProvider extends AbstractSourceProvider implements
		IModelStateChangeListener {

	private static final String pfx = "org.daisy.reader.model."; //$NON-NLS-1$
									   
	private static final String[] SOURCE_NAMES = new String[] {pfx+"IsDTB"}; //$NON-NLS-1$

	private static Map<String, Object> currentState;

	public PropertySourceProvider() {
		currentState = new HashMap<String, Object>();
		currentState.put(SOURCE_NAMES[0], Boolean.FALSE);		
	}

	@Override
	public void initialize(IServiceLocator locator) {
		super.initialize(locator);
		ModelManager.addStateChangeListener(this);
	}

	public void dispose() {
		ModelManager.removeStateChangeListener(this);
	}

	@SuppressWarnings("unchecked")
	public Map getCurrentState() {
		return currentState;
	}

	public String[] getProvidedSourceNames() {
		return SOURCE_NAMES;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.state.IModelStateChangeListener#modelStateChanged(org.daisy.reader.model.state.ModelStateChangeEvent)
	 */
	public void modelStateChanged(ModelStateChangeEvent event) {
		
		if (event.getNewState() == ModelState.DISPOSED) {
			currentState.put(SOURCE_NAMES[0], Boolean.FALSE);			
		} else if (event.getNewState() == ModelState.LOADED) {
			Model model = ModelManager.getModel();
			if(DtbModel.class.isInstance(model)) {
				currentState.put(SOURCE_NAMES[0], Boolean.TRUE);
			}else{
				currentState.put(SOURCE_NAMES[0], Boolean.FALSE);
			}			
		}
		fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW, SOURCE_NAMES[0],
				currentState.get(SOURCE_NAMES[0]));
	}	
}
