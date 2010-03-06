package com.javazoom.javalayer.kit;

import java.net.URL;
import java.util.regex.Pattern;

import org.daisy.reader.model.audio.AudioClipFeeder;
import org.daisy.reader.model.audio.AudioClipPlayer;
import org.daisy.reader.model.audio.BlockingAudioClipQueue;
import org.daisy.reader.model.audio.IAudioKitFactory;
import org.daisy.reader.util.URIStringParser;

public class JLayerAudioKitFactory implements IAudioKitFactory {
	private final Pattern mp3Pattern;
	
	public JLayerAudioKitFactory() {
		mp3Pattern = Pattern.compile(".+\\.[Mm][Pp]3"); //$NON-NLS-1$
	}
	
	public final AudioClipFeeder newClipFeeder(BlockingAudioClipQueue queue) {
		return new JLayerMP3ClipFeeder(queue);
	}

	public final AudioClipPlayer newClipPlayer(BlockingAudioClipQueue queue) {		
		return new JLayerMP3Player(queue);
	}

	public boolean supportsContentType(URL audio) {
		if(audio == null) return false;
		String filename = URIStringParser.getFileLocalName(audio.getPath());
		return mp3Pattern.matcher(filename).matches();
	}

}
