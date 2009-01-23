package org.daisy.reader.model.audio;

import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.smil.AudioClipCursor;
import org.daisy.reader.model.smil.SmilSpine;

/**
 * A blocking queue governing a Producer-Consumer
 * thread relationship.
 * @author Markus Gylling
 */
public class BlockingAudioClipQueue {
    private AudioClip current;
    private boolean available = false;
    private AudioClipCursor cursor;
           
    /**
     * Alternate constructor.
     * @param firstClip a clip to provide directly at the 
     * first call to get.
     */
    public BlockingAudioClipQueue(AudioClip firstClip) {
		current = firstClip;
		available = true;
		cursor = ((SmilSpine)ModelManager.getModel().getSpine()).getAudioClipCursor();
	}
    
    public synchronized AudioClip get() throws InterruptedException {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            	throw e;
            }
        }
        //update cursor before releasing clip
        if(current!=null) cursor.set(current.getSmilAudioMediaObject());        
        available = false;        
        notifyAll();
        return current;
    }

    public synchronized void put(AudioClipFeeder feeder) throws InterruptedException {
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            	throw e;
            }
        }
        current = feeder.getNextAudioClip();
        available = true;        
        notifyAll();
    }
}
