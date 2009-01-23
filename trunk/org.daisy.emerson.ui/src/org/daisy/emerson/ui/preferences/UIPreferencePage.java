package org.daisy.emerson.ui.preferences;

import org.daisy.emerson.ui.Activator;
import org.daisy.emerson.ui.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class UIPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public UIPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());	
	}

	public void createFieldEditors() {
				
		addField(new DirectoryFieldEditor(PreferenceConstants.P_DEFAULT_OPEN_PATH, 
				Messages.UIPreferencePage_DefaultOpenPath, getFieldEditorParent()));		
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_COOLBAR, 
				Messages.UIPreferencePage_ShowCoolBar, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_FORCE_FOCUS_CHANGE, 
				Messages.UIPreferencePage_ForceFocusChange, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
				
	}	

}