package org.daisy.emerson.ui.navigator.handlers;

import java.util.ListIterator;

import org.daisy.emerson.ui.navigator.Messages;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.model.position.URIPosition;
import org.daisy.reader.model.semantic.Semantic;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Navigate to selected page.
 * @author Markus Gylling
 */
public class NavigateSelectedPageHandler extends AbstractHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {		
		Shell shell = HandlerUtil.getActiveShellChecked(event);
		SpinnerDialog dialog = new SpinnerDialog(shell,Messages.NavigateSelectedPageHandler_goToPage);
		dialog.setBlockOnOpen(true);		
		int page = dialog.open();
		if(page>-1) {
			handleActivate(page);
		}
		
		return null;		
	}
	
	private void handleActivate(int page) {
		INavigation navigation = 
			ModelManager.getModel().getNavigation();
		
		ListIterator<INavigationItem> iter = 
			navigation.listIterator(Semantic.PAGE_NUMBER);
		
		while(iter.hasNext()) {
			INavigationItem item = iter.next();			
			if(Integer.parseInt(item.getLabel()) == page) {			 
				 URIPosition destination = new URIPosition(item.getTarget(), navigation.getSourceURL());
				 ModelManager.getModel().setPosition(destination);						 
			}			
		}		
	}
	
	/**
	 * A spinner dialog.
	 * Returns -1 on cancel, or the spinner value on ok.
	 * @author Markus Gylling
	 */
	class SpinnerDialog extends StatusDialog {
		Spinner spinner;
		public SpinnerDialog(Shell shell, String dialogTitle) {			
			super(shell);
			this.setTitle(dialogTitle);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {			
			Composite composite = (Composite)super.createDialogArea(parent);
			addSpinner(composite);
			return composite;
		}
		
		@Override
		protected void okPressed() {
			setReturnCode(spinner.getSelection());
			close();
		}
		
		@Override
		protected void cancelPressed() {
			setReturnCode(-1);
			close();
		}
		private void addSpinner(Composite composite) {
			spinner = new Spinner (composite, SWT.BORDER);		
			GridData gridData = new GridData(SWT.CENTER,SWT.CENTER,true,true);
			spinner.setLayoutData(gridData);
			spinner.setSize(composite.getSize());
			spinner.setDigits(0);
			
			INavigation navigation = 
				ModelManager.getModel().getNavigation();
						
			spinner.setMinimum(getInt(navigation.getFirst(Semantic.PAGE_NUMBER), 1));
			spinner.setMaximum(getInt(navigation.getLast(Semantic.PAGE_NUMBER), 4096));
			spinner.setSelection(getInt(navigation.getCurrent(Semantic.PAGE_NUMBER), spinner.getMinimum()));
								
			spinner.setIncrement(1);
			
			int pageIncr = Math.round(spinner.getMaximum()/10);
			if(pageIncr<1) pageIncr = 1; 
			
			spinner.setPageIncrement(pageIncr);
			
			spinner.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					int sel = spinner.getSelection(); 
					if(sel < spinner.getMinimum()) spinner.setSelection(spinner.getMinimum());					
					if(sel > spinner.getMaximum()) spinner.setSelection(spinner.getMaximum());
				}
			});
			
		}

		private int getInt(INavigationItem item, int fallback) {
			if(item != null) {					
				try{
					return Integer.parseInt(item.getLabel());
				}catch (NumberFormatException e) {
					
				}						
			}
			return fallback;
		}
		
	}

}
