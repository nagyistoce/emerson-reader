package org.daisy.reader.model.d202;

import java.net.URL;

import org.daisy.reader.model.exception.ModelInstantiationException;
import org.daisy.reader.model.provide.IModelProvider;

public class D202ModelProvider implements IModelProvider {

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.IModelProvider#create(java.net.URL)
	 */
	public D202Model create(URL content) throws ModelInstantiationException {		
		try {			
			return D202ModelLoader.load(content);
		} catch (Exception e) {			
			throw new ModelInstantiationException(e.getMessage(),e);
		}
	}


}
