
package org.daisy.reader.model.smil;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.daisy.reader.model.position.PositionTransformer;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.semantic.Semantic;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Maintain the notion of a currently active AudioMediaObject,
 * and traverse the entire presentation bidirectionally.
 * 
 * <p>Note - positions within an audio clip (frames) are not maintained
 * here - an AudioClipCursor only tracks which clip is currently
 * active.</p>
 */
public class AudioClipCursor {
	private AudioMediaObject position;
	private SmilSpine spine;
	private Set<Semantic> skip;
			
	/**
	 * Constructor. Note - the AudioClipCursors position
	 * is null until explicitly set.
	 */
	public AudioClipCursor(SmilSpine spine) {
		this.spine = spine;
		skip=new HashSet<Semantic>(4);
	}
		
	/**
	 * Force a reposition of the cursor
	 * @param newPosition The position to force
	 * @throws IllegalArgumentException If given AudioMediaObject 
	 * is null or not owned by this SmilSpine instance
	 */		
	public void set(AudioMediaObject newPosition) {			
		if(newPosition==null) 
			throw new IllegalArgumentException("newPosition is null"); //$NON-NLS-1$
		
		if(newPosition.getParentContainer().getParentFile().getSpine()!=this.spine) {
			throw new IllegalArgumentException("newPosition is not owned by current SmilSpine"); //$NON-NLS-1$
		}
					
		registerNewPosition(newPosition);			
	}
	
	/**
	 * Retrieve the AudioMediaObject at which this AudioClipCursor
	 * is currently positioned.
	 * @return An AudioMediaObject, never null.
	 */
	public AudioMediaObject current() {
		if(position==null) {
			//self initialization
			position = this.spine.getFirstAudioMediaObject();
		}	
		return position;
	}
	
	/**
	 * Retrieve the next AudioMediaObject in spine order,
	 * and adjust the cursor to this SmilAudioMediaObjects position.
	 * @return The next AudioMediaObject in spine order,
	 * or null if no next AudioMediaObject exists.
	 */
	public AudioMediaObject next() {
		AudioMediaObject next = peekNext();
		if(next!=null) {
			registerNewPosition(next);
		}			
		return next;
	}
	
	/**
	 * Retrieve the previous AudioMediaObject in spine order,
	 * and adjust the cursor to this SmilAudioMediaObjects position.
	 * @return The previous AudioMediaObject in spine order,
	 * or null if no previous AudioMediaObject exists.
	 */
	public AudioMediaObject previous() {
		AudioMediaObject prev = peekPrevious();
		if(prev!=null) {
			registerNewPosition(prev);
		}			
		return prev;
	}
	
	private void registerNewPosition(AudioMediaObject newPosition) {
		position = newPosition;		
		//call back to the model to notify listeners
		final SmilAudioPosition sap = PositionTransformer.toSmilPosition(newPosition);
		
		Display display = PlatformUI.getWorkbench().getDisplay();
		if(!display.isDisposed()) {
			display.asyncExec(new Runnable(){
				public void run() {
					spine.getModel().firePositionChangeEvent(sap);				
				}		
			});		
		}
	}
			
	/**
	 * Retrieve the AudioMediaObject that will
	 * be returned on the next call to next. This
	 * method does not advance the cursor position.
	 * @return The next AudioMediaObject in spine order,
	 * or null if no next AudioMediaObject exists.
	 */
	public AudioMediaObject peekNext() {
		
		TimeContainer currentTimeContainer = position.getParentContainer();
		SmilFile currentSmil = currentTimeContainer.getParentFile();
				
		int nextIndex;
		
		//if the positions owner timecontainer has a next audio, return that		
		List<MediaObject> phraseList = currentTimeContainer.getMediaChildren(AudioMediaObject.class);
		nextIndex = phraseList.indexOf(position)+1;

		while(nextIndex<=phraseList.size()-1) {  
			AudioMediaObject samo = (AudioMediaObject)phraseList.get(nextIndex);
			if(!shouldSkip(samo)) return samo;
			nextIndex++;
		}
		
		//else, if there is a next audio object in the SmilFile as a whole, 
		//return that.		
		nextIndex = currentSmil.indexOf(position)+1;		
	
		while(true) {
			try{  
				AudioMediaObject samo = (AudioMediaObject)currentSmil.get(nextIndex);
				if(!shouldSkip(samo)) return samo;
				nextIndex++;
			}catch (NoSuchElementException e) {
				break;	
			}	
		}
		
		//else if if the position smilfile has a next smilfile, 
		//return the first audio child of that
		nextIndex = this.spine.indexOf(currentSmil)+1;

		while(nextIndex<=this.spine.size()-1) {
			SmilFile nextSmilFile = this.spine.get(nextIndex);
			List<MediaObject> list = nextSmilFile.getFlatAudioList();
			for (int i = 0; i < list.size()-1; i++) {
				AudioMediaObject samo = (AudioMediaObject)list.get(i);
				if(!shouldSkip(samo)) return samo;
			}
			nextIndex++;
		}
		
		//else return null
		return null;
	}
	
	/**
	 * Retrieve the AudioMediaObject that will
	 * be returned on the next call to previous. This
	 * method does not change the cursor position.
	 * @return The previous AudioMediaObject in spine order,
	 * or null if no previous AudioMediaObject exists.
	 */
	public AudioMediaObject peekPrevious() {
		
		TimeContainer currentTimeContainer = position.parent;
		SmilFile currentSmil = currentTimeContainer.getParentFile();
		
		int prevIndex;
		//if the position blocks owner audioChildren list has a prev audio, return that
		List<MediaObject> phraseList = currentTimeContainer.getMediaChildren(AudioMediaObject.class);
		prevIndex = phraseList.indexOf(position)-1;

		while(prevIndex>-1) {
			AudioMediaObject samo = (AudioMediaObject)phraseList.get(prevIndex);
			if(!shouldSkip(samo)) return samo;
			prevIndex--;
		}
				
		//else, if there is a prev audio object in the SmilFile as a whole, 
		//return that.		
		prevIndex = currentSmil.indexOf(position)-1;		

		while(true) {
			try{
				AudioMediaObject samo = (AudioMediaObject)currentSmil.get(prevIndex);
				if(!shouldSkip(samo)) return samo;
				prevIndex--;
			}catch (NoSuchElementException e) {
				break;
			}
		}
		
		//else if if the position smilfile has a prev smilfile, 
		//return the last audio child of that
		prevIndex = this.spine.indexOf(currentSmil)-1;
			
		while(prevIndex>-1) { 
			SmilFile prevSmilFile = this.spine.get(prevIndex);				
			List<MediaObject> list = prevSmilFile.getFlatAudioList();
			for (int i = list.size()-1; i > 0; i--) {
				AudioMediaObject samo = (AudioMediaObject)list.get(i);
				if(!shouldSkip(samo)) return samo;
			}
			prevIndex--;
		}
		
		//else return null
		System.err.println("returning null on AudioCursor#peekPrevious()"); //$NON-NLS-1$
		return null;
	}

	private boolean shouldSkip(AudioMediaObject phrase) {
		if(skip.isEmpty()) return false;
		Semantic sem = phrase.getSemantic(true);
		if(sem!=null && skip.contains(sem)) return true;
		return false;
	}
	
	public void setSkippability(Semantic semantic, Boolean shouldSkip) {
		System.err.println("AudioClipCursor setting skippability for " + semantic.name() + " to " + shouldSkip.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		if(shouldSkip.booleanValue()) {
			skip.add(semantic);
		} else {
			skip.remove(semantic);
		}
	}
	
}