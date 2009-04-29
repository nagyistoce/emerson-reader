package org.daisy.reader.model.z2005;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class Z2005ContentDescriber implements IContentDescriber {

	public Z2005ContentDescriber() {
		
	}

	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		//TODO ContentDescriber, changed extension point in 3.5
		return IContentDescriber.VALID;
	}

	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

}
