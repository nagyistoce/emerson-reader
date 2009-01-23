package org.daisy.emerson.ui.dtb.handlers;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.state.ModelState;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class TogglePlayStopHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Model m = ModelManager.getModel();
		if(m==null || m.isDisposed()) return null;
		return (isRendering(m)) ? m.stop() : m.render();
	}

	private boolean isRendering(Model model) {
		ModelState state = model.getCurrentState();
		return  state == ModelState.READING ||
			state== ModelState.READ_PREPARING;
	}
}

