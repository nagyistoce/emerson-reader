package org.daisy.reader.model.smil;

import java.util.List;

import org.daisy.reader.model.position.SmilAudioPosition;

public class SmilUtils {

	public static SmilClock calcDuration(List<MediaObject> smilAudioList) {
		long sum = 0;
		for(MediaObject mo : smilAudioList) {
			AudioMediaObject au = (AudioMediaObject)mo;
			sum+= au.getDuration();
		}
		return new SmilClock(sum);
	}
	
	/**
	 * Get 0-n TextMediaObjects associated with then given SmilAudioPosition
	 * @param audioPos
	 * @return A list of TextMediaObject
	 */
	public static List<MediaObject> getTextObjects(SmilAudioPosition audioPos) {
		/*
		 * We currently support only the two common cases of 
		 * <par>
		 * 	<text/>
		 *  <seq><audio/><audio/></seq>
		 * </par> 
		 * and
		 * <par>
		 * 	<text/>
		 * 	<audio/>
		 * </par>
		 */ 
		TimeContainer tc = audioPos.getSmilAudioMediaObject().getParentContainer();	
		if(tc instanceof SeqContainer) tc = tc.getParentContainer();
		return tc.getMediaChildren(TextMediaObject.class); 
	}

}
