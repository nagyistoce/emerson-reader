package org.daisy.reader.model.position;

import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.SmilFile;


/**
 * An audio positon within a SMIL presentation (the SmilSpine)
 * constructed by the identity of a SMIL audio element, and an
 * optional time offset measure of the underlying audio resource.
 * @author Markus Gylling
 */
public class SmilAudioPosition implements IPosition {

	private AudioMediaObject phrase = null;
	private Long timeOffset;
	
	/**
	 * Constructor.
	 * @param phrase The AudioMediaObject to represent the structural 
	 * part of this SmilAudioPosition
	 */
	public SmilAudioPosition(AudioMediaObject phrase) {
		if(phrase==null) throw new NullPointerException();
		this.phrase = phrase;
	}
	
	/**
	 * Constructor.
	 * @param phrase The AudioMediaObject to represent the structural 
	 * part of this SmilAudioPosition
	 * @param timeOffset A count in milliseconds that represents the offset
	 * in milliseconds, counting from the start of the audio resource that
	 * this SmilAudioPosition refers to.
	 */
	public SmilAudioPosition(AudioMediaObject phrase, Long timeOffset) {
		this.phrase  = phrase;
		this.timeOffset = timeOffset;
	}
	
	
	
	/**
	 * Get the time offset in milliseconds of the audio resource 
	 * that this SmilAudioPosition refers to, counting from the start 
	 * of the audio resource. If no value has explicitly been set, 
	 * the return value is null.
	 */
	public Long getTimeOffset() {
		return timeOffset;
	}
		
	/**
	 * Set the time offset in milliseconds of the audio resource that this
	 * SmilAudioPosition refers to, counting from the start of the
	 * audio resource.
	 */
	public void setTimeOffset(Long timeOffset) {
		this.timeOffset = timeOffset;		
	}
	
	/**
	 * Get the AudioMediaObject that backs this SmilAudioPosition.
	 * @return A AudioMediaObject or null if this SmilAudioPosition 
	 * instance is not backed by a AudioMediaObject.
	 */
	public AudioMediaObject getSmilAudioMediaObject() {		
		return phrase;
	}
	
	/**
	 * Get the SmilFile to which this SmilAudioPosition refers.
	 * @return
	 */
	public SmilFile getSmilFile() {
		return phrase.getParentContainer().getParentFile();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(phrase!=null) {
			sb.append(phrase.toString());	
			sb.append(" parent container id: " + phrase.getParentContainer().getID()); //$NON-NLS-1$
			if(phrase.getParentContainer().getParentContainer()!=null)
				sb.append(" parent-parent container id: " + phrase.getParentContainer().getParentContainer().getID()); //$NON-NLS-1$
			sb.append(" smilfile: " + phrase.getParentContainer().getParentFile().getLocalName()); //$NON-NLS-1$
		}
		if(this.timeOffset!=null) {
			sb.append("Offset: ").append(timeOffset.toString()); //$NON-NLS-1$
		}
		
		
		return sb.toString();
	}
}