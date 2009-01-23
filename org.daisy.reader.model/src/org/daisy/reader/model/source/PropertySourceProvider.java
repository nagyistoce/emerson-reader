package org.daisy.reader.model.source;

import java.util.HashMap;
import java.util.Map;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Provide Model-related sources for Property testing.
 * @author Markus Gylling
 */

// IEvaluationService service = (IEvaluationService)
// PlatformUI.getWorkbench().getService(IEvaluationService.class);
// service.requestEvaluation(SOURCE_NAMES[0]);

public class PropertySourceProvider extends AbstractSourceProvider implements
		IModelStateChangeListener, IPositionChangeListener {

	private static final String pfx = "org.daisy.reader.model."; //$NON-NLS-1$
	private static final String[] SOURCE_NAMES = new String[] {
			pfx+"IsLoaded", pfx+"HasPages", pfx+"HasNextPage", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			pfx+"HasPrevPage", pfx+"HasNextHeading", pfx+"HasPrevHeading", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			pfx+"IsRendering"}; //$NON-NLS-1$

	private static Map<String, Object> currentState;

	public PropertySourceProvider() {
		currentState = new HashMap<String, Object>();
		currentState.put(SOURCE_NAMES[0], Boolean.FALSE);
		currentState.put(SOURCE_NAMES[1], Boolean.FALSE);
		currentState.put(SOURCE_NAMES[2], Boolean.FALSE);
		currentState.put(SOURCE_NAMES[3], Boolean.FALSE);
		currentState.put(SOURCE_NAMES[4], Boolean.FALSE);
		currentState.put(SOURCE_NAMES[5], Boolean.FALSE);		
		currentState.put(SOURCE_NAMES[6], Boolean.FALSE);
	}

	@Override
	public void initialize(IServiceLocator locator) {
		super.initialize(locator);
		ModelManager.addStateChangeListener(this);
		ModelManager.addPositionChangeListener(this);
	}

	public void dispose() {
		ModelManager.removePositionChangeListener(this);
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
			currentState.put(SOURCE_NAMES[1], Boolean.FALSE);
			currentState.put(SOURCE_NAMES[2], Boolean.FALSE);
			currentState.put(SOURCE_NAMES[3], Boolean.FALSE);
			currentState.put(SOURCE_NAMES[4], Boolean.FALSE);
			currentState.put(SOURCE_NAMES[5], Boolean.FALSE);			
			currentState.put(SOURCE_NAMES[6], Boolean.FALSE);
			fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW, currentState);
			
		} else if (event.getNewState() == ModelState.LOADED) {
			Model model = ModelManager.getModel();
			INavigation nav = model.getNavigation();
			
			//IsLoaded
			currentState.put(SOURCE_NAMES[0], Boolean.TRUE);
		
			//pagination status
			if(nav.getFirst(Semantic.PAGE_NUMBER)!=null) {
				//HasPages
				currentState.put(SOURCE_NAMES[1], Boolean.TRUE);
				
				//HasNextPage
				currentState.put(SOURCE_NAMES[2], Boolean.valueOf(
						nav.getAdjacentPosition(Direction.NEXT, 
								Semantic.PAGE_NUMBER)!=null));
				//HasPrevPage
				currentState.put(SOURCE_NAMES[3], Boolean.valueOf(
						nav.getAdjacentPosition(Direction.PREV, 
								Semantic.PAGE_NUMBER)!=null));	
			}
			
			//heading status
			currentState.put(SOURCE_NAMES[4], Boolean.valueOf(
					nav.getAdjacentPosition(Direction.NEXT, 
							Semantic.HEADING)!=null));
			currentState.put(SOURCE_NAMES[5], Boolean.valueOf(
					nav.getAdjacentPosition(Direction.PREV, 
							Semantic.HEADING)!=null));
						
			fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW, currentState);
		} else if (event.getNewState() == ModelState.READING) {			
			//IsRendering
			fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW,SOURCE_NAMES[6],Boolean.TRUE);
		} else if (event.getNewState() == ModelState.STOPPED) {
			//IsRendering
			fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW,SOURCE_NAMES[6],Boolean.FALSE);
		}			

	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.IPositionChangeListener#positionChanged(org.daisy.reader.model.position.ModelPositionChangeEvent)
	 */
	public void positionChanged(ModelPositionChangeEvent event) {
		//evaluate state of prev and next pages/headings
		//only fire those that changed
				
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){

			public void run() {
				
				final Model model = ModelManager.getModel();
				if(model==null)return;
				final INavigation nav = model.getNavigation();
				if(nav==null)return;
				Boolean eval;
				
				if(((Boolean)currentState.get(SOURCE_NAMES[1])).booleanValue()) {
					//2: next page
					eval = Boolean.valueOf
							(nav.getAdjacentPosition(Direction.NEXT, 
									Semantic.PAGE_NUMBER)!=null);
					eval(eval,SOURCE_NAMES[2]);				
	
					//3: prev page
					eval = Boolean.valueOf
							(nav.getAdjacentPosition(Direction.PREV, 
									Semantic.PAGE_NUMBER)!=null);
					eval(eval,SOURCE_NAMES[3]);				
				}
				
				//4: next heading
				eval = Boolean.valueOf
						(nav.getAdjacentPosition(Direction.NEXT, 
								Semantic.HEADING)!=null);
				eval(eval,SOURCE_NAMES[4]);				

				//5: prev heading
				eval = Boolean.valueOf
						(nav.getAdjacentPosition(Direction.PREV, 
								Semantic.HEADING)!=null);
				eval(eval,SOURCE_NAMES[5]);
				
			}
			
			private boolean eval(Boolean eval, String name) {
				if(!eval.equals(currentState.get(name))) {
					currentState.put(name, eval);
					fireSourceChanged(ISources.ACTIVE_WORKBENCH_WINDOW,name,eval);
					return true;
				}		
				return false;
			}
		});				
		
	}	
}
