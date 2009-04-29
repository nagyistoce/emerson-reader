package org.daisy.reader.model.audio;

import java.net.URL;

import org.daisy.reader.model.smil.AudioMediaObject;

public class AudioClip {
	private long startMillis; 
	private long endMillis;
	private AudioMediaObject phrase;
	private URL url;
	
	public AudioClip(URL url, long startMillis, long endMillis, AudioMediaObject phrase) {
		this.startMillis = startMillis;
		this.endMillis = endMillis;		
		this.url = url;
		this.phrase = phrase;
	}

	public URL getUrl() {
		return url;
	}

	public long getStartMillis() {
		return startMillis;
	}

	public long getEndMillis() {
		return endMillis;
	}
	
	@Override
	public String toString() {		
		return startMillis + "-" + endMillis; //$NON-NLS-1$
	}

	/**
	 * Retrieve the AudioMediaObject that backs this AudioClip
	 * @return
	 */
	public AudioMediaObject getSmilAudioMediaObject() {
		return phrase;
	}
}
