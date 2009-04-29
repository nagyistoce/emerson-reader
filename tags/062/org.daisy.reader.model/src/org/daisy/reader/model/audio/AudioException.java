package org.daisy.reader.model.audio;

public class AudioException extends Exception {
	
	public AudioException(String message) {
		super(message);
	}
	
	public AudioException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = -8378131625785897703L;
}
