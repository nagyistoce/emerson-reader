package org.daisy.emerson.ui.library;

import java.util.List;

import org.daisy.emerson.ui.part.EmersonViewPart;
import org.daisy.reader.history.HistoryEntry;
import org.daisy.reader.history.HistoryEvent;
import org.daisy.reader.history.HistoryList;
import org.daisy.reader.history.IHistoryListener;
import org.daisy.reader.util.User;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * A bookshelf view.
 * @author Markus Gylling
 */
public class LibraryView extends EmersonViewPart implements IDoubleClickListener, IHistoryListener {
	public static final String VIEW_ID = "org.daisy.emerson.ui.views.library";	 //$NON-NLS-1$
	public static final String CONTEXT_ID = "org.daisy.emerson.contexts.views.library"; //$NON-NLS-1$
	private TableViewer tableViewer = null;	
		
	public LibraryView() {
		super(VIEW_ID, CONTEXT_ID);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {			
		super.init(site, memento);		
		HistoryList.addHistoryListener(this);
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.history.IHistoryListener#historyEvent(org.daisy.reader.history.HistoryEvent)
	 */
	public void historyEvent(HistoryEvent event) {
		this.tableViewer.refresh();		
	}

	@Override
	public void dispose() {
		super.dispose();	
		HistoryList.removeHistoryListener(this);		
	}
	
	public void createPartControl(Composite parent) {	
		tableViewer = new TableViewer(parent, 
				SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		TableViewerColumn column1 = new TableViewerColumn(tableViewer, SWT.NONE);
		column1.getColumn().setText(Messages.LibraryView_Title);
		column1.getColumn().setMoveable(false);
		column1.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				HistoryEntry h = (HistoryEntry)element;
				return(h.getPublicationTitle()!=null)
				? h.getPublicationTitle()
				: ""; //$NON-NLS-1$
				  
			}	
			public Image getImage(Object obj) {
				//TODO images per type
				return Activator.getImage("icons/book.gif"); //$NON-NLS-1$
			}
		});
		TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
		column2.getColumn().setText(Messages.LibraryView_Author);
		column2.getColumn().setMoveable(false);
		column2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				HistoryEntry h = (HistoryEntry)element;
				return(h.getPublicationAuthor()!=null)
					? h.getPublicationAuthor()
					: "";				 //$NON-NLS-1$
			}		
		});
		TableViewerColumn column3 = new TableViewerColumn(tableViewer, SWT.NONE);
		column3.getColumn().setText(Messages.LibraryView_Type);
		column3.getColumn().setMoveable(false);
		column3.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				HistoryEntry h = (HistoryEntry)element;
				return(h.getPublicationType().getNiceName()!=null)
				? h.getPublicationType().getNiceName()
				: "";					  //$NON-NLS-1$
			}		
		});
		
		TableColumnLayout layout = new TableColumnLayout();
		layout.setColumnData(column1.getColumn(), new ColumnWeightData(50));
		layout.setColumnData(column2.getColumn(), new ColumnWeightData(40));		
		layout.setColumnData(column3.getColumn(), new ColumnWeightData(10));
		parent.setLayout(layout);
				
		tableViewer.setContentProvider(new ViewContentProvider());
									
		getViewSite().setSelectionProvider(tableViewer);
		
		tableViewer.addDoubleClickListener(this);
				
		init(tableViewer.getControl());
		
		tableViewer.setInput(HistoryList.getInstance());
	}
				
	class ViewContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			
		}

		public void dispose() {
			
		}

		public Object[] getElements(Object parent) {
			List<HistoryEntry> entries = ((HistoryList)parent).get(User.getID());
			return entries.toArray();			
		}
	}
		
	public void doubleClick(DoubleClickEvent event) {
		IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
		try {			
			handlerService.executeCommand(ICommandIDs.LIBRARY_READ_SELECTED,null);
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage(), t);
		}
	} 
}