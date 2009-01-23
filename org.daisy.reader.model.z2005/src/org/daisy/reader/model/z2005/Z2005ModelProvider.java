package org.daisy.reader.model.z2005;

import java.net.URL;

import org.daisy.reader.model.exception.ModelInstantiationException;
import org.daisy.reader.model.provide.IModelProvider;

public class Z2005ModelProvider implements IModelProvider {

	public Z2005ModelProvider() {
		
	}

	public Z2005Model create(URL content) throws ModelInstantiationException {		
		try {			
			return Z2005ModelLoader.load(content);
		} catch (Exception e) {			
			throw new ModelInstantiationException(e.getMessage(),e);
		}
	}

}
