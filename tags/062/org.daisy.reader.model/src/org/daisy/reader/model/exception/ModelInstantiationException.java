package org.daisy.reader.model.exception;

import org.daisy.reader.model.Messages;

public class ModelInstantiationException extends Exception {
	private final static String PFX = Messages.ModelInstantiationException_prefix;
	
	public ModelInstantiationException(String message, Throwable cause) {
		super(PFX + message, cause);
	}

	private static final long serialVersionUID = -8118240988657675333L;
}
