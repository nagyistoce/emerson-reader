package org.daisy.reader.model.exception;

import org.daisy.reader.model.Messages;

public class UnsupportedContentTypeException extends Exception {
	private final static String PFX = Messages.UnsupportedContentTypeException_prefix;
	
	public UnsupportedContentTypeException(String message) {
		super(PFX + message);
	}
	
	public UnsupportedContentTypeException(String message, Throwable cause) {		
		super(PFX + message, cause);
	}
	
	private static final long serialVersionUID = -8378131625785897703L;
}
