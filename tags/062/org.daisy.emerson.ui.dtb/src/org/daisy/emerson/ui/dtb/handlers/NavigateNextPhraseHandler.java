package org.daisy.emerson.ui.dtb.handlers;

import org.daisy.reader.model.navigation.Direction;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class NavigateNextPhraseHandler extends NavigateAdjacentPhraseHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.direction = Direction.NEXT;
		return super.execute(event);
	}
}
