package org.daisy.emerson.ui.navigator.handlers;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.state.ModelState;
import org.eclipse.core.commands.AbstractHandler;

/**
 * Navigate to a select position of a given semantic.
 * @author Markus Gylling
 */
public abstract class NavigateSelectedHandler extends AbstractHandler {
	
	protected boolean navigateTo(IPosition position) {
		Model model = ModelManager.getModel();
		return model.setPosition(position);
	}
	
	protected boolean isDisposed(Model model) {
		ModelState state = model.getCurrentState();
		return state== ModelState.DISPOSING ||
			state== ModelState.DISPOSED;
	}
}
