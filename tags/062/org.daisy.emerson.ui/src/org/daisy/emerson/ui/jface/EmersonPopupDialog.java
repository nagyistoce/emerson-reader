package org.daisy.emerson.ui.jface;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;

/**
 * Abstract base for all popup-style dialogs in the application.
 * At this level, all that is provided is generic layout and styling,
 * and a set of callbacks for customization and population.
 * @author Markus Gylling
 */
public abstract class EmersonPopupDialog extends PopupDialog {
	
	protected IWorkbench workbench = null;
	protected IWorkbenchPart activePart = null;
	protected IHandlerService handlerService = null;
	protected ICommandService commandService = null;
	protected IBindingService bindingService = null;
	
	
	public EmersonPopupDialog (IWorkbenchPart activePart, IWorkbench workbench,  
			Shell parent, String titleText,  String infoText) {
		
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, true, true, true,
			true, true, titleText, infoText);
		
		this.workbench = workbench;
		this.activePart = activePart;
		this.handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
		this.commandService = (ICommandService) workbench.getService(ICommandService.class);
		this.bindingService = (IBindingService) workbench.getService(IBindingService.class);
						
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {

		// First, register the shell type with the context support
		registerShellType();

		// Create a composite for the dialog area.
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginHeight = 10;
		compositeLayout.marginWidth = 10;
		composite.setLayout(compositeLayout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setBackground(parent.getBackground());

		final Display display = PlatformUI.getWorkbench().getDisplay();		
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
	    Color black = display.getSystemColor(SWT.COLOR_BLACK);
		applyBackgroundColor(white, composite);
		applyForegroundColor(black, composite);
		composite.setBackground(white);
		composite.setForeground(white);
		
		doCreateDialogArea(composite);

		doApplyDialogFont(composite);
						
		doRegisterListeners();
		
		return composite;
	}
	
	/**
	 * A callback from {@link RaymondDialog#createDialogArea(Composite)}.
	 * Subclasses can override this method for packing etc.
	 * @param dialogComposite The Composite to act upon.
	 */
	protected void doApplyDialogFont(Composite dialogComposite) {
		Dialog.applyDialogFont(dialogComposite);		
	}

	/**
	 * A callback from {@link RaymondDialog#createDialogArea(Composite)}.
	 * Subclasses will override this method to add widgets (tables, trees, etc).
	 * @param dialogComposite The Composite to register widgets with.
	 */
	protected abstract void doCreateDialogArea(Composite dialogComposite);

	/**
	 * A callback from {@link RaymondDialog#createDialogArea(Composite)}.
	 * Subclasses overrides this method to register listeners.
	 */
	protected abstract void doRegisterListeners();
	
	/**
	 * Registers the shell as the same type as its parent with the context
	 * support. This ensures that it does not modify the current state of the
	 * application.
	 */
	private final void registerShellType() {
		final Shell shell = getShell();

		final IContextService contextService = 
			(IContextService) workbench.getService(IContextService.class);
		
		contextService.registerShell(shell, contextService
				.getShellType((Shell) shell.getParent()));
	}
}
