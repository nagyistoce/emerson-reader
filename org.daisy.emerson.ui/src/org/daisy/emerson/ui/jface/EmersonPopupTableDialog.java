package org.daisy.emerson.ui.jface;

import org.daisy.emerson.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Abstract base for table-based popup dialogs.
 * @author Markus Gylling
 */
public abstract class EmersonPopupTableDialog extends EmersonPopupDialog {
	
	protected Table table = null;
	
	public EmersonPopupTableDialog (IWorkbenchPart activePart, IWorkbench workbench, 
			Shell parent, String titleText) {		
		this(activePart, workbench, parent, titleText, Messages.EmersonPopupTableDialog_EscToClose);		
	}
	
	public EmersonPopupTableDialog (IWorkbenchPart activePart, IWorkbench workbench, 
			Shell parent, String titleText, String infoText) {		
		super(activePart, workbench, parent, titleText, infoText);		
	}

	@Override
	protected void doCreateDialogArea(Composite dialogComposite) {
		table = new Table(dialogComposite, getTableStyle());
		final GridData gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);
		table.setBackground(dialogComposite.getBackground());
		table.setLinesVisible(true);
		
		doCreateTableColumns(); //for subclasses to implement
		
		doCreateTableItems();	//for subclasses to implement
		
	}
	
	/**
	 * For subclasses to override. The default implementation
	 * creates two columns.
	 */
	protected void doCreateTableColumns() {
		new TableColumn(table, SWT.LEFT, 0);
		new TableColumn(table, SWT.LEFT, 1);		
	}

	/**
	 * Create the table items. For subclasses to implement.
	 */
	protected abstract void doCreateTableItems();
	
	/**
	 * For subclasses to override. The default implementation
	 * creates an SWT.FULL_SELECTION | SWT.SINGLE st.
	 */
	protected int getTableStyle() {		
		return SWT.FULL_SELECTION | SWT.SINGLE;
	}
	
	/**
	 * For subclasses to override. The default implementation
	 * registers left mouseclick and CR with a callback to 
	 * doHandleactivate
	 */
	@Override	
	protected void doRegisterListeners() {
		table.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
				doHandleActivate(e);						
			}

			public void mouseDown(MouseEvent e) {}

			public void mouseUp(MouseEvent e) {}
			
		});
		
		table.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {}

			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR || e.character == ' ') { 
					doHandleActivate(e);
				}				
			}			
		});
	}
	
	@Override
	protected void doApplyDialogFont(Composite dialogComposite) {		
		super.doApplyDialogFont(dialogComposite);
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableColumn tc = columns[i];
			tc.pack();
		}		
	}
	
	/**
	 * For subclasses to override. Default do-nothing callback 
	 * from the default key and mouse listeners of RaymondTableDialog.
	 * @see #doRegisterListeners()
	 */
	protected void doHandleActivate(TypedEvent e) {
		
	}
	
	@Override
	protected Control getFocusControl() {
		return table;
	}
	
	/**
	 * Get the selected TableItem (in case of SWT.SINGLE) or
	 * the first selected (in case of SWT.MULTI).
	 * @return A TableItem or null if none is selected
	 */
	protected TableItem getSelected() {
		 TableItem[] selected = table.getSelection();
		 if(selected!=null && selected.length > 0) {					 
			 return selected[0];	
		 }	 
		 return null;
	}
	
}