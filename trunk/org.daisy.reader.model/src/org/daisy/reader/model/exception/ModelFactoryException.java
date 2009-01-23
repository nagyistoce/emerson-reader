package org.daisy.reader.model.exception;

import org.daisy.reader.model.Messages;

public class ModelFactoryException extends Exception {
	private final static String PFX = Messages.ModelFactoryException_prefix;
	
	public ModelFactoryException(String message, Throwable cause) {
		super(PFX + message, cause);
	}
	
	private static final long serialVersionUID = -9127193493729715898L;
}
