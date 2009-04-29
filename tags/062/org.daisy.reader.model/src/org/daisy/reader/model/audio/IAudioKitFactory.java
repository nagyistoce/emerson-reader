package org.daisy.reader.model.audio;

import java.net.URL;

/**
 * A concrete instance of an IAudioKit has a zero-argument
 * constructor, and can provide instances of AudioClipPlayer
 * and AudioClipFeeder (whom together with the given
 * BlockingAudioClipQueue forms a triad AudioKit).
 * @author Markus Gylling
 */
public interface IAudioKitFactory {
		
	public AudioClipPlayer newClipPlayer(BlockingAudioClipQueue queue);
	
	public AudioClipFeeder newClipFeeder(BlockingAudioClipQueue queue);
	
	public boolean supportsContentType(URL audio);
	
}
