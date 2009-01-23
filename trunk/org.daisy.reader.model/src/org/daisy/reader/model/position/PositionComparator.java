package org.daisy.reader.model.position;

import java.util.Comparator;

import org.daisy.reader.Activator;
import org.daisy.reader.model.smil.SmilSpine;

public final class PositionComparator implements Comparator<IPosition> {

	private SmilSpine spine;
	
	public PositionComparator(SmilSpine spine) {
		this.spine = spine;
	}
	
    /**
     * Compare two IPosition instances. Returns a negative integer,
	 * zero, or a positive integer as the first IPosition occurs
	 * before, at the same time, or after the second.
     */
	public int compare(IPosition ipos1, IPosition ipos2) {
		try{
			//make sure we have types that can be compared
			IPosition pos1 = convert(ipos1);
			IPosition pos2 = convert(ipos2);
			
			//first try to compare only smil index
			int index1 = getSmilIndex(pos1);
			int index2 = getSmilIndex(pos2);
			if(index1 != index2)
				return index1 - index2;
			
			//then compare audio phrase index
			index1 = getPhraseIndex(pos1);
			index2 = getPhraseIndex(pos2);
			if(index1 != index2)
				return index1 - index2;
			
			//then compare millis position
			index1 = getMillisIndex(pos1);
			index2 = getMillisIndex(pos2);
			if(index1 != index2)
				return index1 - index2;
			
		}catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
		}
		return 0;
	}
	
	private int getMillisIndex(IPosition pos) {
		return (pos instanceof SmilAudioPosition) 
		? ((SmilAudioPosition)pos).getTimeOffset().intValue() 
				: (((SerializableSmilAudioPosition)pos).timeOffset).intValue();
	}

	private int getPhraseIndex(IPosition pos) {
		return (pos instanceof SmilAudioPosition) 
		? ((SmilAudioPosition)pos).getSmilFile().indexOf(
					((SmilAudioPosition)pos).getSmilAudioMediaObject()) 
				: ((SerializableSmilAudioPosition)pos).phraseIndex;		
	}

	private int getSmilIndex(IPosition pos) {
		return (pos instanceof SmilAudioPosition) 
			? spine.indexOf(((SmilAudioPosition)pos).getSmilFile().getLocalName()) 
					: spine.indexOf(((SerializableSmilAudioPosition)pos).smilFileName);		
	}

	/**
	 * Make sure given IPosition is a SmilAudioPosition or 
	 * a SerializableSmilAudioPosition
	 */
	private IPosition convert(IPosition pos) {
		return (pos instanceof SmilAudioPosition || pos instanceof SerializableSmilAudioPosition) 
			? pos  : PositionTransformer.toSmilPosition(pos) ;		
	}


}
