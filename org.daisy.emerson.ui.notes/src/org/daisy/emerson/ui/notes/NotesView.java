package org.daisy.emerson.ui.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.daisy.emerson.ui.part.EmersonViewPart;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.daisy.reader.notes.INotesListener;
import org.daisy.reader.notes.Note;
import org.daisy.reader.notes.NoteEvent;
import org.daisy.reader.notes.Notes;
import org.daisy.reader.util.StringUtils;
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

public class NotesView extends EmersonViewPart implements IDoubleClickListener, 
	IModelStateChangeListener, INotesListener {
	
	public static final String CONTEXT_ID = "org.daisy.emerson.contexts.views.notes"; //$NON-NLS-1$
	public static final String VIEW_ID = "org.daisy.emerson.ui.views.notes"; //$NON-NLS-1$
	public static TableViewer tableViewer = null;

	private static SimpleDateFormat dateFormat = null;
		
	public NotesView() {
		super(VIEW_ID, CONTEXT_ID);		
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		ModelManager.addStateChangeListener(this);
		Notes.addListener(this);
	}
			
	public void notesChanged(NoteEvent event) {
		tableViewer.refresh(true);		
	}

	public void modelStateChanged(ModelStateChangeEvent event) {
		if(event.getNewState() == ModelState.DISPOSING) {
			tableViewer.setInput(new Object());
		}else if (event.getNewState() == ModelState.LOADED) {
			tableViewer.setInput(Notes.getInstance());
		}
	}

	public void createPartControl(Composite parent) {	
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
				
		TableViewerColumn column1 = new TableViewerColumn(tableViewer, SWT.NONE);
		column1.getColumn().setText(Messages.NotesView_Context);
		column1.getColumn().setMoveable(false);
		column1.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				Note m = (Note)element;
				return m.getContext();  
			}			
			public Image getImage(Object obj) {
				return Activator.getImage("icons/bookmark.gif"); //$NON-NLS-1$
			}
		});
		TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
		column2.getColumn().setText(Messages.NotesView_Note);
		column2.getColumn().setMoveable(false);
		column2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				Note m = (Note)element;
				return StringUtils.normalizeWhitespace((m.getContent()));
			}		
		});
		
		TableViewerColumn column3 = new TableViewerColumn(tableViewer, SWT.NONE);
		column3.getColumn().setText(Messages.NotesView_Date);
		column3.getColumn().setMoveable(false);
		column3.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				Note m = (Note)element;
				if(m.getTimeStamp() != -1){				
					return getDateFormat().format(new Date(m.getTimeStamp()));
				}	
				return ""; //$NON-NLS-1$
			}		
		});
		
		TableColumnLayout layout = new TableColumnLayout();
		layout.setColumnData(column1.getColumn(), new ColumnWeightData(20));
		layout.setColumnData(column2.getColumn(), new ColumnWeightData(60));
		layout.setColumnData(column3.getColumn(), new ColumnWeightData(20));
		parent.setLayout(layout);
		
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
				
			}

			public void dispose() {
				
			}

			public Object[] getElements(Object parent) {
				if(parent instanceof Notes) {
					Model model = ModelManager.getModel();
					if(model!=null && !model.isDisposed()) {
						String pid;
						PublicationType type;
						try {
							pid = (String)model.getProperty(IPropertyConstants.PUBLICATION_UUID);
							type = (PublicationType)model.getProperty(IPropertyConstants.PUBLICATION_TYPE);
						} catch (PropertyException e) {
							return new Object[0];							
						}
						return ((Notes)parent).get(pid, User.getID(),type).toArray();
					}
				}
				return new Object[0];
			}
		});
				
		getViewSite().setSelectionProvider(tableViewer);
		tableViewer.addDoubleClickListener(this);
		tableViewer.setInput(Notes.getInstance());
		
		init(tableViewer.getControl());
				
	}
	
	private SimpleDateFormat getDateFormat() {
		if(dateFormat==null) {
			dateFormat = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss", //$NON-NLS-1$
				Locale.getDefault());
		}
		return dateFormat;
	}
	
	@Override
	public void dispose() {		
		tableViewer.getControl().dispose();
		ModelManager.removeStateChangeListener(this);
		Notes.removeListener(this);
		super.dispose();
	}
				
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {		
		IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
		try {			
			handlerService.executeCommand(ICommandIDs.NOTE_DEREFERENCE,null);
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage(), t);
		}		
	}
	



	

}