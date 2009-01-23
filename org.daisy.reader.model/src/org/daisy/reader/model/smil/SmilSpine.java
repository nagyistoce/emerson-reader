package org.daisy.reader.model.smil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.daisy.reader.Activator;
import org.daisy.reader.model.Model;
import org.daisy.reader.util.URLUtils;

/**
 * Represent the entire DTB SMIL presentation, which is physically spread
 * out between 1-n W3C SMIL files.
 * 
 * <p>The SmilSpine maintains positional state through the AudioClipCursor. 
 * There is always one <em>current</em> 
 * <code>AudioMediaObject</code> instance (but not necessarily a 
 * <em>next</em>) which can be accessed via the AudioClipCursor.</p>
 * 
 * <p>The finest resolution of a presentation position that the SmilSpine can 
 * provide is that of a AudioMediaObject (or "phrase"). Any finer resolution 
 * (such as audio frame offsest) is out of scope for the SmilSpine to maintain.</p>
 * 
 * <p>TODO The SmilFile objects in the list use transparent lazy initialization. 
 * The SmilSpine always returns playable SmilFiles, since it substitutes 
 * erroneous ones with a message placeholder.</p> 
 * 
 * @author Markus Gylling
 */
public class SmilSpine extends ArrayList<SmilFile> {
	private AudioClipCursor audioClipCursor;
	//private SmilUnloaderDaemon unloaderDaemon;
	private SmilClock mDuration;
	private Model model;
	

	/**
	 * Constructor.
	 */
	public SmilSpine() {
		//System.err.println("SmilSpine constructor");
		audioClipCursor = new AudioClipCursor(this);			
		//unloaderDaemon = new SmilUnloaderDaemon(this); //TODO enable, prob with navigation access
	}
	
	public void setModel(Model model) {
		this.model = model;
	}
	
	public Model getModel() {
		return this.model;
	}
	
	/**
	 * Construction time method. Evaluate a String URI and potentially add it 
	 * as a SmilFile to the spine.
	 * @param ref A fragmentless relative URI (from the referer)
	 * @param base The URL of the referer in which the fragmentless 
	 * relative URI appears.
	 * @param loader An ISmilLoader instance for later lazy parsing of the SMIL file.
	 * @throws MalformedURLException
	 */
	public void add(String ref, URL base, ISmilLoader loader) throws MalformedURLException {					
		URL url = URLUtils.resolve(base, ref);		
		if (this.contains(url))		
			return;		
		this.add(new SmilFile(url, loader, this));
	}
	
	/**
	 * Does this SmilSpine contain a SmilFile with the given URL
	 * @return true if this SmilSpine contains a SmilFile with the given URL
	 */	
	public boolean contains(URL url) {
		//override the super impl since URL equals is slow	
		Iterator<SmilFile> it = this.iterator(false);
		while (it.hasNext()) {
			SmilFile smilFile = it.next();
			if(smilFile.url.getPath().equals(url.getPath())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieve a SmilFile which matches the given URL, or
	 * null if no matching SmilFile exists in this SmilSpine instance.
	 */
	public SmilFile get(URL smilURL) {
		//use a non-statechecking iterator to find the right entry
		Iterator<SmilFile> iterator = this.iterator(false);
		while(iterator.hasNext()) {
			SmilFile smil = iterator.next();
			if (smil.url.getPath().equals(smilURL.getPath()))
				return checkState(smil);
		}			
		return null;
	}
	
	/**
	 * Retrieve a SmilFile which matches the given localName case sensitively, or
	 * null if no matching SmilFile exists in this SmilSpine instance.
	 * @param localName the name to match, case sensitively
	 */
	public SmilFile get(String localName) {
		return this.get(localName,true);
	}
		
	/**
	 * Retrieve a SmilFile which matches the given localName, or
	 * null if no matching SmilFile exists in this SmilSpine instance.
	 * @param localName The name to match
	 * @param caseSensitive Whether to make a case sensitive match
	 */
	public SmilFile get(String localName, boolean caseSensitive) {		
		String match = localName;
		if(!caseSensitive) 
			match = match.toLowerCase();
		
		//use a non-statechecking iterator to find the right entry
		Iterator<SmilFile> iterator = this.iterator(false);
		while(iterator.hasNext()) {
			SmilFile smil = iterator.next();
			String name = smil.getLocalName();
			if(!caseSensitive) 
				name= name.toLowerCase();			
			if (name.equals(match))
				return checkState(smil);
		}
		return null;
	}
	
	@Override
	public SmilFile get(int index) {		
		return this.get(index,true);
	}
	
	public SmilFile get(int index, boolean doStateCheck) {		
		if(doStateCheck)
			return checkState(super.get(index));
		return super.get(index);
	}
	
	public SmilFile getFirst() {
		return checkState(this.get(0));
	}
	
	public AudioMediaObject getFirstAudioMediaObject() {
		for(SmilFile smil : this) {
			if(smil.getFirstAudioMediaObject()!=null) {
				return smil.getFirstAudioMediaObject();
			}
		}
		//TODO : no audio in book. Could play an error audio file
		return null;
	}

	@Override
	public Iterator<SmilFile> iterator() {		
		return new StateCheckingIterator(this,true);
	}
	
	public Iterator<SmilFile> iterator(boolean stateCheck) {		
		return new StateCheckingIterator(this, stateCheck);
	}
	
	@Override
	public ListIterator<SmilFile> listIterator() {		
		return new StateCheckingIterator(this,true);
	}
	@Override
	public ListIterator<SmilFile> listIterator(int index) {
		return new StateCheckingIterator(this,index,true);
	}
	
	public int indexOf(String smilFileLocalName) {
		int i = -1;
		for(Iterator<SmilFile> iter = iterator(false); iter.hasNext();) {
			i++;
			SmilFile sf = iter.next();
			if(sf.getLocalName().equals(smilFileLocalName)) return i; 
		}
		return -1;
	}
	
	/**
	 * Get the AudioClipCursor instance that tracks
	 * current position (in terms of audio objects)
	 * in the presentation.
	 */
	public AudioClipCursor getAudioClipCursor() {
		return audioClipCursor;
	}
	
//	public SmilUnloaderDaemon getSmilUnloaderDaemon() {
//		return unloaderDaemon;
//	}
	/**
	 * An iterator over SmilSpine's SmilFile list whose default behavior
	 * is to load SmilFiles as they become the iterators current item. 		
	 */
	private class StateCheckingIterator implements ListIterator<SmilFile> {
		private int index = -1 ;
		private SmilSpine spine;
		private boolean doStateCheck;
		
		public StateCheckingIterator(SmilSpine spine, boolean doStateCheck) {
			this.spine = spine;
			this.doStateCheck = doStateCheck;
		}
		
		public StateCheckingIterator(SmilSpine spine, int index, boolean doStateCheck) {
			this.spine = spine;
			this.index = index;
			this.doStateCheck = doStateCheck;
		}

		public void add(SmilFile e) {
			spine.add(e);
		}

		public boolean hasNext() {
			if(spine.size()==0)return false;
			return spine.size()>(index+1);
		}

		public boolean hasPrevious() {
			return spine.size()>0 && index>0;
		}

		public SmilFile next() {			
			if(hasNext()) {
				if(doStateCheck)
					return checkState(spine.get(++index));
				return spine.get(++index, doStateCheck);
			}
			throw new NoSuchElementException();
		}

		public int nextIndex() {
			if(index==spine.size()-1) return spine.size();
			return index +1;
		}

		public SmilFile previous() {			
			if(hasPrevious()) {
				if(doStateCheck)
					return checkState(spine.get(--index));
				return spine.get(--index);
			}
			return null;
		}

		public int previousIndex() {			
			if(index==0) return -1;
			return index - 1;
		}

		public void remove() {
			spine.remove(index);			
		}

		public void set(SmilFile e) {
			spine.set(index, e);			
		}		
	}
	
	/**
	 * Check the state of a lazily loaded SmilFile and load if 
	 * needed before returning. If a SMIL file was not loaded
	 * successfully, let a placeholder error SMIL file
	 * take its place.
	 */
	private SmilFile checkState(SmilFile smil) {
		if(smil==null) throw new IllegalStateException(new NullPointerException());
		if(smil.getState()== SmilFile.State.UNPARSED) {
			//System.err.println("SmilSpine loading " + smil.getLocalName());
			if(smil.load() != SmilFile.State.PARSED_OK) {
				//TODO placeholder error SmilFile, insert into Spine
				System.err.println("NOT DONE: should return placeholder error SmilFile"); //$NON-NLS-1$
			}
		}
		return smil;
	}

	public void setDuration(String clockValue) {
		try{
			mDuration = new SmilClock(clockValue);
		}catch (NumberFormatException e) {
			Activator.getDefault().logError(e.getMessage(), e);
		}
	}
	
	/**
	 * Get the duration of the SMIL presentation as a whole,
	 * or a SmilClock with duration 0 if no duration could be calculated.
	 */
	public SmilClock getDuration() {
		if(mDuration==null) {
			//TODO here we parse all SmilFiles, could use 
			//an iterator without statecheck			
			long sum = 0;
			for(SmilFile sf : this) {
				SmilClock sc;
				try{
					sc = sf.getDuration();
				}catch (Exception e) {
					continue;
				}
				sum+=sc.millisecondsValue();
			}
			mDuration = new SmilClock(sum);
		}
		return mDuration;
	}
	
	private static final long serialVersionUID = -5761332440274091456L;

}