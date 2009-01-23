package org.daisy.reader.notes;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Provide Notes-related sources for Property testing.
 * @author Markus Gylling
 */

public class PropertySourceProvider extends AbstractSourceProvider implements
		INotesListener {

	private static final String pfx = "org.daisy.reader.notes."; //$NON-NLS-1$
	private static final String[] SOURCE_NAMES = new String[] {
			pfx+"IsEmpty"}; //$NON-NLS-1$

	private static Map<String, Object> currentState;

	public PropertySourceProvider() {
		currentState = new HashMap<String, Object>();
		currentState.put(SOURCE_NAMES[0], Boolean.TRUE);		
	}

	@Override
	public void initialize(IServiceLocator locator) {
		super.initialize(locator);
		Notes.addListener(this);
	}

	public void dispose() {
		Notes.removeListener(this);
	}

	@SuppressWarnings("unchecked")
	public Map getCurrentState() {
		return currentState;
	}

	public String[] getProvidedSourceNames() {
		return SOURCE_NAMES;
	}

	public void notesChanged(NoteEvent event) {
		currentState.put(SOURCE_NAMES[0], 
				Notes.getInstance().entries.isEmpty() 
					? Boolean.TRUE : Boolean.FALSE);	
		
		fireSourceChanged(
				ISources.ACTIVE_WORKBENCH_WINDOW, SOURCE_NAMES[0], 
					currentState.get(SOURCE_NAMES[0]));
	}	
}
