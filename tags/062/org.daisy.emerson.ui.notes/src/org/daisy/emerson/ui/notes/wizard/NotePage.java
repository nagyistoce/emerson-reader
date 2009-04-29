package org.daisy.emerson.ui.notes.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class NotePage extends WizardPage {
	private boolean mTextChanged;
	protected Text text;
	
	protected NotePage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {	      
		parent.setLayout(new GridLayout(2, false));

		text = new Text(parent, SWT.BORDER | SWT.MULTI );
		textChanged();
		
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.verticalSpan = 24;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		text.setLayoutData(data);
		
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});
		
		text.addTraverseListener(new TraverseListener(){
			public void keyTraversed(TraverseEvent e) {
				if(e.detail == SWT.TRAVERSE_TAB_NEXT) {
					e.doit = true;
				}				
			}			
		});
		setControl(parent);
	}

	private void textChanged() {
		mTextChanged = true;
	}

	String getNote() {
		return text.getText();
	}
	
	boolean getTextChanged() {
		return mTextChanged;
	}
	
}
