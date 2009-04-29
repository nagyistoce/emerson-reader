package org.daisy.reader.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.memento.IPositionMementoSupport;
import org.daisy.reader.model.position.memento.PositionMementoSupportFactory;
import org.daisy.reader.model.property.PublicationType;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ui.IMemento;

/**
 * A collection of Note, supporting IMemento instantiation and persisting. 
 * The set spans users and publications.
 * @author Markus Gylling
 */

public class Notes {
	
	private static Notes instance = new Notes();	
	private static ListenerList listeners;
	List<Note> entries;
	
	private Notes() {				
		entries = new CopyOnWriteArrayList<Note>();
		listeners = new ListenerList(ListenerList.IDENTITY);
	}
	
	public static Notes getInstance() {
		return instance;
	}
	
	public static void create(IMemento source) {
		
		if(source==null) return;
		Set<Note> created = new HashSet<Note>();
		
		IMemento[] entries = source.getChildren(NOTE_ENTRY);
		for (int i = 0; i < entries.length; i++) {
			IMemento sub = entries[i];
			
			String context = sub.getString(NOTE_CONTEXT);
			String content = sub.getString(NOTE_CONTENT);
			String pubUid = sub.getString(NOTE_PUBUID);
			String userID = sub.getString(NOTE_USERID);	
			
			Long date = null;
			try{
				String dateString = sub.getString(NOTE_DATE);				
				if(dateString!=null)
					date = Long.parseLong(dateString);
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}
			
			Integer sequence = sub.getInteger(SEQUENCE);
			
			PublicationType type = new PublicationType(
					sub.getString(PUB_TYPE_NICENAME),
						sub.getString(PUB_TYPE_TECHNAME),
							sub.getString(PUB_TYPE_IMPLEMENTOR));
			
			IAutonomousPosition pos = null;
			String positionType = sub.getString(POS_TYPE);			
			if(positionType!=null) {
				try{
					IPositionMementoSupport deserializer = 
						PositionMementoSupportFactory.newInstance()
							.create(Class.forName(positionType));
					pos = deserializer.deserialize(sub);
				}catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);		
				}
			}
			created.add(new Note(context,date,pos,content,pubUid,userID,sequence,type));			
		} //for 
		Notes.getInstance().addAll(created);		
	}
	
	private void addAll(Set<Note> created) {
		this.entries.addAll(created);
		fireAddedEvent(created);		
	}

	public IMemento serialize(IMemento destination) {
		for(Note note : entries) {
			try{
				IMemento sub = destination.createChild(NOTE_ENTRY);
				sub.putString(NOTE_CONTEXT, note.context);
				sub.putString(NOTE_CONTENT, note.note);
				sub.putString(NOTE_PUBUID, note.publicationUID);
				sub.putString(NOTE_USERID, note.userID);
				sub.putString(NOTE_DATE,Long.toString(note.date));				
				sub.putString(SEQUENCE,Integer.toString(note.sequence));
				sub.putString(PUB_TYPE_NICENAME, note.publicationType.getNiceName());
				sub.putString(PUB_TYPE_TECHNAME, note.publicationType.getTechName());
				sub.putString(PUB_TYPE_IMPLEMENTOR, note.publicationType.getImplementor());
				try {
					IPositionMementoSupport serializer = 
						PositionMementoSupportFactory.newInstance()
							.create(note.location.getClass());
					if(serializer!=null) {
						sub.putString(POS_TYPE, note.location.getClass().getName());						
						serializer.serialize(note.location, sub);
					}else{
						Activator.getDefault().logError(
								"No serializer for position type "  //$NON-NLS-1$
								+ note.location.getClass().getName(), new IllegalStateException());
					}
				} catch (Exception e) {
					Activator.getDefault().logError(e.getLocalizedMessage(), e);				
				}				
			}catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);				
			}
		}
		return destination;
	}
	
	private static final String NOTE_ENTRY = "NoteEntry"; //$NON-NLS-1$
	private static final String NOTE_CONTEXT = "NoteContext"; //$NON-NLS-1$
	private static final String NOTE_CONTENT = "NoteContent"; //$NON-NLS-1$
	private static final String NOTE_PUBUID = "NotePublicationUID"; //$NON-NLS-1$
	private static final String NOTE_USERID = "NoteUserID"; //$NON-NLS-1$
	private static final String NOTE_DATE = "NoteDate"; //$NON-NLS-1$
	private static final String SEQUENCE = "NoteSequence"; //$NON-NLS-1$
	private static final String POS_TYPE = "PositionType"; //$NON-NLS-1$
	private static final String PUB_TYPE_NICENAME = "PublicationTypeNiceName"; //$NON-NLS-1$
	private static final String PUB_TYPE_TECHNAME = "PublicationTypeTechName"; //$NON-NLS-1$
	private static final String PUB_TYPE_IMPLEMENTOR = "PublicationTypeImplementor"; //$NON-NLS-1$
	
	/**
	 * Retrieve a list of BookMarks associated with given publication
	 *  UID, type and user ID. 
	 * <p>Changes made to the returned list are not persisted.<p>
	 * @param publicationUID The UID of the publication to which the 
	 * returned Notes refer.
	 * @param userUID The UID of the user who owns the markers.
	 * @return A list with 0-n markers, never null. The list is sorted 
	 * using Note.sequence
	 */
	public List<Note> get(String publicationUID, String userUID,
			PublicationType type) {
		List<Note> ret = new ArrayList<Note>();
		for(Note note : entries) {
			if(note.publicationUID.equals(publicationUID) 
					&& note.userID.equals(userUID)
					&& note.publicationType.equals(type)) {
				ret.add(note);
			}
		}
		Collections.sort(ret, new Comparator<Note>(){

			public int compare(Note arg0, Note arg1) {				
				if(arg0.sequence<arg1.sequence) return -1;
				else if(arg0.sequence>arg1.sequence) return 1;
				
				if(arg0.date<arg1.date) return -1;
				else if(arg0.date>arg1.date) return 1;
				
				return 0;
			}
		});
		return ret;
	}
			
	/**
	 * Add a Note.
	 * @param marker The marker to add
	 */
	public void add(Note note) {		
		entries.add(note);
		fireAddedEvent(note);
	}
	
	/**
	 * Replace a Note.
	 * @return true if replacement succeeded, else false.
	 */
	public boolean replace(Note oldNote, Note newNote) {
		int i = 0;
		for(Note note : entries) {
			if(note == oldNote) {
				entries.set(i, newNote);
				fireReplacedEvent(oldNote, newNote);
				return true;
			}
			++i;
		}
		return false;
	}
	
	/**
	 * Delete a Note.
	 * @return true if deletion succeeded, else false.
	 */
	public boolean remove(Note note) {
		if(entries.remove(note)) {
			fireDeletedEvent(note);
			return true;
		}
		return false;
	}

	public static void addListener(INotesListener listener) {
		listeners.add(listener);
	}
	
	public static void removeListener(INotesListener listener) {
		listeners.remove(listener);
	}
	
	private void fireAddedEvent(Note marker) {
		NoteAddedEvent event = new NoteAddedEvent(this, marker);
		iterateListeners(event);		
	}
	
	private void fireAddedEvent(Set<Note> added) {
		NotesAddedEvent event = new NotesAddedEvent(
				this, added.toArray(new Note[added.size()]));
		iterateListeners(event);	
	}
	
	private void fireDeletedEvent(Note marker) {
		NoteDeletedEvent event = new NoteDeletedEvent(this, marker);
		iterateListeners(event);	
	}
	
	private void fireReplacedEvent(Note replaced, Note replacer) {
		NoteReplacedEvent event = new NoteReplacedEvent(this, replaced, replacer);
		iterateListeners(event);		
	}
	
	private void iterateListeners(NoteEvent event) {
		Object[] listening = listeners.getListeners();
		for (int i = 0; i < listening.length; ++i) {
			((INotesListener) listening[i]).notesChanged(event);
		}
	}
}
