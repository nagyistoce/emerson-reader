package org.daisy.reader.model.audio;

/**
 * Abstract base for the producer role in a classic
 * producer-consumer thread relationship where
 * BlockingAudioClipQueue is the middle man.
 * 
 * <p>Through the override of getNextAudioClip, the
 * concrete AudioClipFeeder can provide specializations
 * of AudioClip that target the consuming AudioClipPlayer.</p>
 * @author Markus Gylling
 */
public abstract class AudioClipFeeder extends Thread {
	
	private BlockingAudioClipQueue clipQueue;
	private boolean isDisposed = false;
	
	public AudioClipFeeder(BlockingAudioClipQueue queue) {
		clipQueue = queue;
	}
	
	public void run() {		
		while(this.isDisposed == false) {
			try {													
				clipQueue.put(this);
			} catch (InterruptedException e) {
				close();
				break;
			}			
		}
	}
		
	/**
	 * Return the next AudioClip to be pushed into the queue
	 * as soon as the consumer has grabbed the previous one.
	 * <p>Note - if the AudioClipCursor is used to get clips,
	 * the implementation of this method needs to peek, since
	 * this method is called while the previous clip is still
	 * active (playing).
	 */
	protected abstract AudioClip getNextAudioClip();
		
	public void close() {
		isDisposed = true;
	}

}
