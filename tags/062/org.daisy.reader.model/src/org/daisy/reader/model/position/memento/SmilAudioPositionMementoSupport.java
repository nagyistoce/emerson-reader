package org.daisy.reader.model.position.memento;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.PositionTransformer;
import org.daisy.reader.model.position.SerializableSmilAudioPosition;
import org.eclipse.ui.IMemento;

public class SmilAudioPositionMementoSupport implements IPositionMementoSupport {

	private static final String SMIL_AUDIO_POSITION = "smil-audio-position"; //$NON-NLS-1$
	private static final String SMIL_FILE_NAME = "smil-file"; //$NON-NLS-1$
	private static final String PHRASE_INDEX = "phrase-offset"; //$NON-NLS-1$
	private static final String MILLIS_OFFSET = "millis-offset"; //$NON-NLS-1$

	public SmilAudioPositionMementoSupport() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.memento.IPositionMementoSupport#deserialize(org.eclipse.ui.IMemento)
	 */
	public IAutonomousPosition deserialize(IMemento source) throws URISyntaxException, MalformedURLException {		
		
		IMemento sub = source.getChild(SMIL_AUDIO_POSITION); 
		
		Long millis = null;
		String s = sub.getString(MILLIS_OFFSET); 				
		if(s!=null) millis = Long.parseLong(s);
						
		return
			new SerializableSmilAudioPosition(sub.getInteger(PHRASE_INDEX),
					sub.getString(SMIL_FILE_NAME), millis);						
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.memento.IPositionMementoSupport#serialize(org.daisy.reader.model.position.IPosition, org.eclipse.ui.IMemento)
	 */
	public void serialize(IPosition position, IMemento destination) throws URISyntaxException {		
		SerializableSmilAudioPosition sap		
			= PositionTransformer.toSerializableSmilAudioPosition(position);
				
		IMemento sub = destination.createChild(SMIL_AUDIO_POSITION);
		sub.putString(SMIL_FILE_NAME, sap.getSmilFileName());
		sub.putInteger(PHRASE_INDEX, sap.getPhraseOffset());
		if(sap.getTimeOffset()!=null)
			sub.putString(MILLIS_OFFSET, sap.getTimeOffset().toString());			
				
	}
	
}
