package org.daisy.reader.model.position;

/**
 * A less efficient version of SmilAudioPosition that can be serialized
 * and does not depend on pointers to SmilSpine or other instance data.
 * @see IAutonomousPosition
 * @author Markus Gylling
 */
public class SerializableSmilAudioPosition implements IAutonomousPosition {	
	String smilFileName;	
	int phraseIndex = 0;
	Long timeOffset;

	/**
	 * Constructor.
	 * @param phraseIndex May be null.
	 * @param smilFileName Cannot be null.
	 * @param timeOffset May be null.
	 * @throws IllegalArgumentException if smilFileName is null
	 */
	public SerializableSmilAudioPosition(int phraseIndex, String smilFileName, Long timeOffset) {
		this.phraseIndex = phraseIndex;
		this.smilFileName = smilFileName;
		this.timeOffset = timeOffset;
	}

	/**
	 * The local name of the SMIL file to which this position refers.
	 * <p>This method never returns null.<p>
	 */
	public String getSmilFileName() {
		return smilFileName;
	}

	/**
	 * Get the position of audio element/phrase within the SMIL file 
	 * to which this position refers.
	 * <p>This method never returns null.<p>
	 */
	public int getPhraseOffset() {
		return phraseIndex;
	}

	/**
	 * Get the time offset in milliseconds  of the audio resource 
	 * that this SmilAudioPosition refers to, counting from the start 
	 * of the audio resource. If no value has explicitly been set, 
	 * the return value is null.
	 */
	public Long getTimeOffset() {
		return timeOffset;
	}

//	public void serialize(IMemento destination) {
//		IMemento sub = destination.createChild(SMIL_AUDIO_POSITION);
//		sub.putString(SMIL_FILE_NAME, getSmilFileName());
//		sub.putInteger(PHRASE_INDEX, getPhraseOffset());
//		if(getTimeOffset()!=null)
//			sub.putString(MILLIS_OFFSET, getTimeOffset().toString());		
//	}
//	
//	public static SerializableSmilAudioPosition deserialize(IMemento source) {
//		IMemento sub = source.getChild(SMIL_AUDIO_POSITION); 
//					
//		Long millis = null;
//		String s = sub.getString(MILLIS_OFFSET); 				
//		if(s!=null) millis = Long.parseLong(s);
//						
//		return
//			new SerializableSmilAudioPosition(sub.getInteger(PHRASE_INDEX),
//					sub.getString(SMIL_FILE_NAME), millis);		
//		
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("file: " + this.smilFileName); //$NON-NLS-1$
		sb.append(" phraseIndex: " + this.phraseIndex); //$NON-NLS-1$
		sb.append(" millisOffset: " + this.timeOffset); //$NON-NLS-1$
		return sb.toString();
	}
	
//	private static final String SMIL_AUDIO_POSITION = "smil-audio-position"; //$NON-NLS-1$
//	private static final String SMIL_FILE_NAME = "smil-file"; //$NON-NLS-1$
//	private static final String PHRASE_INDEX = "phrase-offset"; //$NON-NLS-1$
//	private static final String MILLIS_OFFSET = "millis-offset"; //$NON-NLS-1$


	
}
