package org.daisy.emerson.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DefaultPerspective implements IPerspectiveFactory {
	/** Default Perspective id. */
	public static final String PERSPECTIVE_ID = "org.daisy.emerson.ui.perspectives.default"; //$NON-NLS-1$
    /** Left folder's id. */
    public static final String FOLDER_LEFT_ID = PERSPECTIVE_ID + ".leftFolder"; //$NON-NLS-1$
    /** Right folder's id. */
    public static final String FOLDER_RIGHT_ID = PERSPECTIVE_ID + ".rightFolder"; //$NON-NLS-1$
    /** Bottom folder's id. */
    public static final String FOLDER_BOTTOM_ID = PERSPECTIVE_ID + ".bottomFolder"; //$NON-NLS-1$
    
	public void createInitialLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(false);
		layout.setFixed(false);				
		final String EDITOR_AREA_ID = layout.getEditorArea();
				
		layout.createPlaceholderFolder(FOLDER_LEFT_ID, IPageLayout.LEFT, 0.75f, EDITOR_AREA_ID);       		
		layout.createPlaceholderFolder(FOLDER_BOTTOM_ID, IPageLayout.BOTTOM, 0.75f, FOLDER_LEFT_ID);
		
	}
	
	
	
	
}
