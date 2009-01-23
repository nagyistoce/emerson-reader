package org.daisy.emerson.ui.notes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.emerson.ui.notes.messages"; //$NON-NLS-1$
	public static String CreateNotePage_AddNewNote;
	public static String CreateNotePage_NewNote;
	public static String CreateNoteWizard_AddNote;
	public static String EditNotePage_EditNote;
	public static String EditNotePage_EditNoteContent;
	public static String EditNoteWizard_EditNote;
	public static String NotesView_Context;
	public static String NotesView_Date;
	public static String NotesView_Note;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
