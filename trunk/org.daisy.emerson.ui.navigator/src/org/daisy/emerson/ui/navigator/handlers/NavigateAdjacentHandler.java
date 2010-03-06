package org.daisy.emerson.ui.navigator.handlers;

import org.daisy.emerson.ui.navigator.Activator;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.semantic.Semantic;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Navigate to adjacent position of a given semantic.
 * <p>We use this abstract class instead of commandparameters because we need 
 * separate handler activation depending on whether a specific semantic has a 
 * prev or next entry.</p>
 * @author Markus Gylling
 */
public abstract class NavigateAdjacentHandler extends AbstractHandler {
	protected Direction direction;
	protected Semantic semantic;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try{
			Model model = ModelManager.getModel();
			if(model!=null && !model.isDisposed()) {
				IPosition position = model.getAdjacentPosition(direction, semantic);
				if(position!=null) {
					model.setPosition(position);
				}
			}
		}catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
		}
		return null;
	}
	
}
