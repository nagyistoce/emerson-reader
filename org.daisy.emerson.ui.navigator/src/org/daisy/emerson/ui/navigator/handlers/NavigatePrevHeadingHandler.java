package org.daisy.emerson.ui.navigator.handlers;

import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.semantic.Semantic;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Navigate to adjacent position of a given semantic.
 * @author Markus Gylling
 */
public class NavigatePrevHeadingHandler extends NavigateAdjacentHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.direction = Direction.PREV;
		super.semantic = Semantic.HEADING;
		return super.execute(event);
	}
}
