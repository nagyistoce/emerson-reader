package org.daisy.reader.history;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.PositionTransformer;
import org.daisy.reader.model.position.memento.IPositionMementoSupport;
import org.daisy.reader.model.position.memento.PositionMementoSupportFactory;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.model.state.IModelStateChangeListener;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.daisy.reader.util.User;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ui.IMemento;

/**
 * Maintain a list of publications accessed in an install
 * of the application. The history list spans users.
 * 
 * <p>The list should only contain one entry per publication and user.
 * Previous records of a publication read by a user should be removed
 * when inserting new entries (HistoryEntry.equals tests this, 
 * and HistoryList.add implements this).</p>
 * 
 * <p>This model can be used for bookshelf-like
 * functionality, to store last user positions, etc.</p>
 *  
 * @author Markus Gylling
 */

public class HistoryList implements IModelStateChangeListener  {
	
	private List<HistoryEntry> entries;
	private static ListenerList listeners = null;
	private static HistoryList instance = new HistoryList();
	
	private static int MAX_LIST_SIZE; //TODO could be a settable user prop 
		
	private HistoryList() {
		this(255);		
		ModelManager.addStateChangeListener(this);
	}

	public void dispose() {
		ModelManager.removeStateChangeListener(this);
	}
	
	private HistoryList(int size) {
		entries = new CopyOnWriteArrayList<HistoryEntry>();
		listeners = new ListenerList(ListenerList.IDENTITY);
		MAX_LIST_SIZE = size;
	}
	
	public static HistoryList getInstance() {
		return instance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.state.IModelStateChangeListener#modelStateChanged(org.daisy.reader.model.state.ModelStateChangeEvent)
	 */
	public void modelStateChanged(ModelStateChangeEvent event) {
		ModelState newState = event.getNewState();
		if(newState== ModelState.LOADED||newState== ModelState.DISPOSING) {		
			add(event.getSource(),(newState== ModelState.LOADED)?true:false);
		}		
	}

	/**
	 * Convenience method. Create a HistoryEntry based on
	 * current model state, and add it at the first position
	 * of the HistoryList. Delete any previous record that
	 * matches on user and publication id.
	 * @param Model the Model to create a history entry from
	 * @param maintainPreviousPosition Whether the position in 
	 * a previous entry should be copied to the new entry 
	 * @return true if the entry was successfully added
	 */
	private boolean add(Model model, boolean maintainPreviousPosition) {
		if(model!=null) {
			try {				
				String pubID = (String)model.getProperty(IPropertyConstants.PUBLICATION_UUID);
				String userID = User.getID();
				URL pubURL = (URL)model.getProperty(IPropertyConstants.PUBLICATION_URL);
				String title = (String)model.getProperty(IPropertyConstants.PUBLICATION_TITLE);
				String author = (String)model.getProperty(IPropertyConstants.PUBLICATION_AUTHOR);
				PublicationType type = (PublicationType)model.getProperty(IPropertyConstants.PUBLICATION_TYPE);
				IAutonomousPosition position = PositionTransformer.toAutonomousPosition(model.getPosition());
				HistoryEntry newEntry = new HistoryEntry(
						position,pubID,pubURL,userID,title,author,type);
				
				//delete any previous entry of the same pub and the same user				
				HistoryEntry oldEntry = this.get(newEntry);	
				if(oldEntry != null) {
					if(maintainPreviousPosition) newEntry.position = oldEntry.position;
					entries.remove(oldEntry);
				}
				
				//add our new one, first.
				entries.add(0, newEntry);
				
				//if we are exceeding max size
				if(entries.size()>MAX_LIST_SIZE) {
					entries.remove(entries.size()-1);
				}
				
 				fireEvent(new HistoryEvent(instance,newEntry,HistoryEvent.Type.ADDED));
 				
 				return true;
			} catch (Exception e) {
				Activator.getDefault().logError(e.getMessage(), e);			
			}
		}
		return false;
	}
	
//	private void add(HistoryEntry entry) {
//		entries.add(entry);		
//	}

	/**
	 * Element-changing operations on this iterator (remove, set, and add) are not supported. 
	 * These methods throw UnsupportedOperationException. 
	 */
	public Iterator<HistoryEntry> iterator() {
		return entries.iterator();
	}
	
	/**
	 * Remove a HistoryEntry from this HistoryList.
	 * @param entry The HistoryEntry to remove
	 * @return true if this list contained the specified element
	 */
	public boolean remove(HistoryEntry entry) {		
		if(entries.remove(entry)) {						
			fireEvent(new HistoryEvent(instance,entry,HistoryEvent.Type.REMOVED));
			return true;			
		}
		return false;
	}

	private void fireEvent(HistoryEvent event) {
		Object[] listening = listeners.getListeners();
		for (int i = 0; i < listening.length; ++i) {
			((IHistoryListener) listening[i]).historyEvent(event);
		}
	}

	/**
	 * Return a HistoryEntry that matches given publication
	 * ID and user ID.
	 */
	public HistoryEntry get(String publicationID, String userID, PublicationType publicationType) {
		for(HistoryEntry in : entries) {
			if(in.publicationUID.equals(publicationID)
					&& in.userUID.equals(userID)
					&& in.publicationType.equals(publicationType)) {
				return in;
			}
		}
		return null;
	}
	
	/**
	 * Get an array of all HistoryEntry instances that match
	 * given User ID.
	 * <p>The list is sorted per recency.</p>
	 * @param userID
	 * @return An array, empty or not.
	 */
	public List<HistoryEntry> get(String userID) {
		List<HistoryEntry> ret = new ArrayList<HistoryEntry>();
		for(HistoryEntry entry : entries) {
			if(entry.getUserUID().equals(userID)) {
				ret.add(entry);
			}
		}
		if(!ret.isEmpty()) {
			Collections.sort(ret, new EntryComparator());
			Collections.reverse(ret);
		}
		return ret;
	}
	
	/**
	 * Return a HistoryEntry that equals given entry.
	 * <p>HistoryEntry equality is based on User ID
	 * and Publication ID.</p>
	 */
	private HistoryEntry get(HistoryEntry compare) {
		for(HistoryEntry hi : entries) {
			if(hi.equals(compare)) {
				return hi;
			}
		}
		return null;
	}

	public IMemento serialize(IMemento destination) {				
		//HistoryList.sort(this); //shouldnt be needed, list is always chronological	
		
		int count = 1;
		for(HistoryEntry entry : entries ) {
			if(count>MAX_LIST_SIZE) break;			
			
			String uri;
			
			try {
				uri = entry.lastManifestLocation.toURI().toString();
			} catch (URISyntaxException e) {
				Activator.getDefault().logError(e.getMessage(), e);
				continue;
			}
			
			
			IMemento sub = destination.createChild(HISTORY_ENTRY);
			if(entry.position!=null) {
				try {
					//write the position
					IPositionMementoSupport serializer = 
						PositionMementoSupportFactory.newInstance()
							.create(entry.position.getClass());
					if(serializer!=null) {
						sub.putString(POS_TYPE, entry.getLastPosition().getClass().getName());						
						serializer.serialize(entry.position, sub);
//						System.err.println("Serialized the position "
//								+ entry.position.toString() + " for " + entry.publicationTitle
//								+ ": " + entry.getPublicationType().toString());
					}else{
						throw new NullPointerException(
								"No serializer for " + entry.position.getClass().getName()); //$NON-NLS-1$
					}
				} catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);					
				}
			}
			
			//write the simple stuff
			sub.putString(PID, entry.publicationUID);
			sub.putString(AUTHOR, entry.publicationTitle);
			sub.putString(TITLE, entry.publicationAuthor);
			sub.putString(UID, entry.userUID);
			sub.putString(DATE, Long.toString(entry.entryLastAccessDate));
			sub.putString(URI, uri);
			sub.putString(TYPE_NICENAME,entry.publicationType.getNiceName());
			sub.putString(TYPE_TECHNAME,entry.publicationType.getTechName());
			sub.putString(TYPE_IMPLEMENTOR,entry.publicationType.getImplementor());
			
			
			count++;
		}
		
		
		return destination; 
	}
	
	
	private static final String HISTORY_ENTRY = "HistoryEntry"; //$NON-NLS-1$
	private static final String POS_TYPE = "PositionType"; //$NON-NLS-1$
	private static final String PID = "PubId"; //$NON-NLS-1$
	private static final String UID = "UserId"; //$NON-NLS-1$
	private static final String DATE = "Date"; //$NON-NLS-1$
	private static final String URI = "Uri"; //$NON-NLS-1$
	private static final String TITLE = "Title"; //$NON-NLS-1$
	private static final String AUTHOR = "Author"; //$NON-NLS-1$
	private static final String TYPE_NICENAME = "TypeNiceName"; //$NON-NLS-1$
	private static final String TYPE_TECHNAME = "TypeTechName"; //$NON-NLS-1$
	private static final String TYPE_IMPLEMENTOR = "TypeImplementor"; //$NON-NLS-1$
	
	/**
	 * Create a HistoryList from an IMemento and make it the singleton instance
	 * of this class.
	 * @see HistoryList#getInstance()
	 * @param source
	 */
	public static void create(IMemento source) {
		List<HistoryEntry> newList = new ArrayList<HistoryEntry>();
		if(source==null) return;
		
		IMemento[] entries = source.getChildren(HISTORY_ENTRY);
		for (int i = 0; i < entries.length; i++) {
			if(i>MAX_LIST_SIZE) break;
			IMemento entry = entries[i];
			//System.err.println("reading one historyentry");
			
			//recreate the position
			IAutonomousPosition pos = null;
			String positionType = entry.getString(POS_TYPE);
			
			if(positionType!=null) {
				try{
					IPositionMementoSupport deserializer = 
						PositionMementoSupportFactory.newInstance()
							.create(Class.forName(positionType));
					pos = deserializer.deserialize(entry);
				}catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);		
				}
			}
 
			String typeNiceName = entry.getString(TYPE_NICENAME);
			String typeTechName = entry.getString(TYPE_TECHNAME);
			String typeImplementor = entry.getString(TYPE_IMPLEMENTOR);
			PublicationType type = new PublicationType(typeNiceName,typeTechName,typeImplementor);
			
			//recreate the simple stuff
			String pid = entry.getString(PID);
			String uid = entry.getString(UID);
			String title = entry.getString(TITLE);
			String author = entry.getString(AUTHOR);
			
			URL url;
			try {
				url = new URI(entry.getString(URI)).toURL();
			} catch (MalformedURLException e) {
				Activator.getDefault().logError(e.getMessage(), e);
				continue;
			} catch (URISyntaxException e) {
				Activator.getDefault().logError(e.getMessage(), e);
				continue;
			}
			
			Long date = null;
			try{
				String dateString = entry.getString(DATE);				
				if(dateString!=null)
					date = Long.parseLong(dateString);
			}catch (Exception e) {
				Activator.getDefault().logError(e.getMessage(), e);
			}			
			
			newList.add(new HistoryEntry(pos,pid,url,uid,date,title,author,type));
			
		}
		//HistoryList.sort(newList); //shouldnt be needed, list is always chronologicalList
		instance.entries.addAll(newList);
	}
		
//	/**
//	 * Sort the history entries so the youngest
//	 * appear first 
//	 */
//	private static void sort(HistoryList list) {
//		Collections.sort(list.entries, new EntryComparator());
//		Collections.reverse(list.entries);
//	}
	
	/**
    * @param o1 the first object to be compared.
    * @param o2 the second object to be compared.
    * @return a negative integer, zero, or a positive integer as the
    * 	       first argument is less than, equal to, or greater than the
    *	       second.
    */
	static class EntryComparator implements Comparator<HistoryEntry> {
		public int compare(HistoryEntry he1, HistoryEntry he2) {		
			return (int)he1.entryLastAccessDate - (int)he2.entryLastAccessDate;
		}		
	}
	
	public HistoryEntry getMostRecent() {
		return (entries.isEmpty() ? null : entries.get(0));		
	}
	
	public static void addHistoryListener(IHistoryListener listener) {
		listeners.add(listener);
	}
	
	public static void removeHistoryListener(IHistoryListener listener) {
		listeners.remove(listener);
	}
	private static final long serialVersionUID = 1477957243370352752L;
	
}
