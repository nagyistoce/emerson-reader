package org.daisy.reader.model.provide;

import java.net.URL;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.exception.ModelInstantiationException;

/**
 * Interface for providers of IModels.
 * @author Markus Gylling
 */
public interface IModelProvider {

	String EXTENSION_POINT_ID = "org.daisy.reader.model.provider"; //$NON-NLS-1$
	
	/**
	 * Create an {@link Model} instance. This method never returns null.
	 * @throws ModelInstantiationException if instantiation fails.
	 */
	public Model create(URL content) throws ModelInstantiationException;
		
}
