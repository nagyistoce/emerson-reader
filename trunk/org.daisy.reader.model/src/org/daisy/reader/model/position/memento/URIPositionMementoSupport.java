package org.daisy.reader.model.position.memento;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.URIPosition;
import org.eclipse.ui.IMemento;

public class URIPositionMementoSupport implements IPositionMementoSupport {

	public URIPositionMementoSupport() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.memento.IPositionMementoSupport#deserialize(org.eclipse.ui.IMemento)
	 */
	public IAutonomousPosition deserialize(IMemento source) throws URISyntaxException, MalformedURLException {		
		IMemento child = source.getChild(CHILD_TYPE);
		return new URIPosition(child.getString(IDENTIFIER),new URI(child.getString(REFERER)).toURL());		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.memento.IPositionMementoSupport#serialize(org.daisy.reader.model.position.IPosition, org.eclipse.ui.IMemento)
	 */
	public void serialize(IPosition position, IMemento destination) throws URISyntaxException {		
		IMemento child = destination.createChild(CHILD_TYPE);
		child.putString(IDENTIFIER, ((URIPosition)position).getIdentifier());
		child.putString(REFERER, ((URIPosition)position).getReferer().toURI().toASCIIString());
	}

	private static final String CHILD_TYPE = "uri-position"; //$NON-NLS-1$
	private static final String IDENTIFIER = "identifier"; //$NON-NLS-1$
	private static final String REFERER = "refererer"; //$NON-NLS-1$
}
