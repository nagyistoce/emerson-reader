package org.daisy.reader.model.audio;

import org.daisy.reader.model.ModelManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Abstract base for the consumer role in a classic
 * producer-consumer thread relationship where
 * BlockingAudioClipQueue is the middle man.
 * @author Markus Gylling
 */
public abstract class AudioClipPlayer extends Thread {
	
	private boolean isDisposed = false;
	
	private BlockingAudioClipQueue clipQueue;
	
	protected AudioClipPlayer(BlockingAudioClipQueue queue) {
		clipQueue = queue;
	}
	
	@Override
	public void run() {
		while(!isDisposed) {
			AudioClip clip = null;
			try {
				clip = clipQueue.get();
				
				if(clip==null) {
					handleEndOfBook();	
					//close();
					dispose();
					break;
				}
								
				try {
					set(clip);
					play();
					
				} catch (AudioException e) {
					e.printStackTrace();
					//Activator.getDefault().logError(e.getMessage(), e);
				}
			} catch (InterruptedException e) {
				//close();
				dispose();
				break;			
			}
			
		}
		dispose();
	}
	
	private void handleEndOfBook() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable(){
			public void run() {
				//TODO shouldnt have this dependency
				ModelManager.getModel().stop();				
			}
		});			
	}

	/**
	 * Dispose the player and release all associated
	 * resources.
	 */
	protected abstract void dispose();
				
	/**
	 * Register a first or next clip to play.
	 * @throws AudioException If initializing this clip
	 * caused an irrecoverable exception in the audio system.
	 * @throws InterruptedException If the given audioclip
	 * is null, which is assumed to mean the end of book
	 * @see #play()
	 */
	protected abstract void set(AudioClip clip) throws AudioException;
	
	/**
	 * Play the clip that was registered in {@link #set(AudioClip)}
	 */
	protected abstract void play() throws AudioException;
	
	public boolean isDisposed() {
		return isDisposed;
	}
		
	/**
	 * Flag that this AudioClipPlayer should be disposed at
	 * the next appropriate opportunity.
	 */
	public void close() {
		isDisposed = true;
	}

	/**
	 * Get the current time in milliseconds, or null
	 * if the current time can not be retrieved.
	 */
	public abstract Long getCurrentTime();
	
	
	public void setProperty(String key, Object value) {
		
	}
	
	public Object getProperty(String key) {
		return null;
	}



		
}
