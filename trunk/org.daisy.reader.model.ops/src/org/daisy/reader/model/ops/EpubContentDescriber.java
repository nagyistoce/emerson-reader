package org.daisy.reader.model.ops;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class EpubContentDescriber implements IContentDescriber{

	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		return IContentDescriber.VALID;
	}

	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

}
