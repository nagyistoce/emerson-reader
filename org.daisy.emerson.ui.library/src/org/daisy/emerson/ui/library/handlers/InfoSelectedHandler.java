package org.daisy.emerson.ui.library.handlers;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.daisy.emerson.ui.jface.EmersonPopupTableDialog;
import org.daisy.emerson.ui.library.Messages;
import org.daisy.reader.history.HistoryEntry;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Show a dialog with information on selected HistoryEntry
 * @author Markus Gylling
 */
public class InfoSelectedHandler extends AbstractHandler {
		
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection!=null && selection instanceof StructuredSelection) {
			if(!selection.isEmpty()) {
				final StructuredSelection sel = (StructuredSelection)selection;					
	        	HistoryEntry entry = (HistoryEntry)sel.getFirstElement();	        				
	        	HistoryEntryDialog dialog = new HistoryEntryDialog(entry,event);	 
	        	dialog.open();
			}
		}
		return null;
	}
	
	class HistoryEntryDialog extends EmersonPopupTableDialog {

		private HistoryEntry entry;

		public HistoryEntryDialog(HistoryEntry entry, ExecutionEvent event) {						
			super(HandlerUtil.getActivePart(event), 
					HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench(), 
					HandlerUtil.getActiveShell(event), Messages.InfoSelectedHandler_PublicationInformation);
			this.entry = entry;
		}

		@Override
		protected void doCreateTableItems() {
			Map<String,String> map = new LinkedHashMap<String,String>();
			map.put(Messages.InfoSelectedHandler_Title, entry.getPublicationTitle());
			map.put(Messages.InfoSelectedHandler_Author, entry.getPublicationAuthor());
			map.put(Messages.InfoSelectedHandler_Type, entry.getPublicationType().getNiceName() + " (" + entry.getPublicationType().getTechName() +")"); //$NON-NLS-1$ //$NON-NLS-2$
			map.put(Messages.InfoSelectedHandler_File, entry.getLastManifestLocation().getPath());
			map.put(Messages.InfoSelectedHandler_LastAccess, new Date(entry.getLastAccessDate()).toString()); //TODO i18n?
			
			for (String key : map.keySet()) {
				final String[] text = {key, map.get(key)};
				final TableItem item = new TableItem(table, SWT.NULL);
				item.setText(text);	
			}
		
		}
		
				
	}
}
