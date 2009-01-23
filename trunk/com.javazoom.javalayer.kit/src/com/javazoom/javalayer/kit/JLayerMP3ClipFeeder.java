package com.javazoom.javalayer.kit;

import javazoom.jl.decoder.JavaLayerException;

import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.audio.AudioClip;
import org.daisy.reader.model.audio.AudioClipFeeder;
import org.daisy.reader.model.audio.BlockingAudioClipQueue;
import org.daisy.reader.model.smil.AudioClipCursor;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.SmilSpine;

public final class JLayerMP3ClipFeeder extends AudioClipFeeder {
	private AudioClipCursor cursor = null;
	private AudioMediaObject peeked = null;
	
	public JLayerMP3ClipFeeder(BlockingAudioClipQueue queue) {
		super(queue);
		cursor = ((SmilSpine)ModelManager.getModel().getSpine()).getAudioClipCursor();
	}
	
	@Override
	protected AudioClip getNextAudioClip() {

		//prep the next
		peeked = cursor.peekNext(); 		
		if(peeked==null) return null; //end of book
				
		//optimize for the jlayercontext, to minimize gap
		//between phrases. 
		//We have time since this is called while previous
		//phrase is playing
		JLayerMP3Clip jclip = null;
		try {
			jclip = new JLayerMP3Clip(peeked.getURL(), 
					peeked.getStartClock().millisecondsValue(), 
					peeked.getEndClock().millisecondsValue(),peeked);
			
		} catch (JavaLayerException e) {
			Activator.getDefault().logError(e.getMessage(), e);
		} 				
		return jclip;		
	}	

}
