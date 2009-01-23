package org.daisy.emerson.ui.dtb.preferences;

import org.daisy.emerson.ui.dtb.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class SkippabilityPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SkippabilityPage() {
		super(GRID);
		setPreferenceStore(PlatformUI.getPreferenceStore());	
	}

	public void createFieldEditors() {
		
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.P_SKIP_PAGES,
				Messages.getString("SkippabilityPage.skipPages"),  //$NON-NLS-1$
				getFieldEditorParent()));

		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_SKIP_FOOTNOTES,
					Messages.getString("SkippabilityPage.skipFootnotes"),  //$NON-NLS-1$
					getFieldEditorParent()));

		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_SKIP_PRODNOTES,
					Messages.getString("SkippabilityPage.skipProducerNotes"),  //$NON-NLS-1$
					getFieldEditorParent()));


		addField(
				new BooleanFieldEditor(
					PreferenceConstants.P_SKIP_SIDEBARS,
					Messages.getString("SkippabilityPage.skipSidebars"),  //$NON-NLS-1$
					getFieldEditorParent()));
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		
	}
	
}