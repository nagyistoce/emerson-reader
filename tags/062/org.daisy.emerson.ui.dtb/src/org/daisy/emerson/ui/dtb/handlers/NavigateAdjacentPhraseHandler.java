package org.daisy.emerson.ui.dtb.handlers;

import org.daisy.emerson.ui.dtb.Activator;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.semantic.Semantic;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class NavigateAdjacentPhraseHandler extends AbstractHandler {
	protected Direction direction;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try{
			
			Model model = ModelManager.getModel();
			
			if(model!=null && !model.isDisposed()) {
				IPosition position = 
					model.getAdjacentPosition(direction, Semantic.PHRASE);
				
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
