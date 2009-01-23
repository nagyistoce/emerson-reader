package org.daisy.emerson.ui;

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.internal.presentations.classic.WorkbenchPresentationFactoryClassic;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

@SuppressWarnings("restriction")
public class AccessiblePresentationFactory extends WorkbenchPresentationFactoryClassic {

	public AccessiblePresentationFactory() {
		super();
	}	
	
	@Override
	public StackPresentation createViewPresentation(Composite parent, IStackPresentationSite site) {

		StackPresentation sp = super.createViewPresentation(parent, site);
		
		for (Control control : ((Composite) sp.getControl()).getChildren()) {
			
			if (control instanceof ToolBar) {
				control.getAccessible().addAccessibleListener(
						new AccessibleAdapter() {
							@Override
							public void getName(AccessibleEvent e) {
								e.result = Messages.AccessiblePresentationFactory_ViewMenu;
							}							
						}
				);
			}
		}
		return sp;
	}

}
