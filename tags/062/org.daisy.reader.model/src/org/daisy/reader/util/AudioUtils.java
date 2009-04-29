package org.daisy.reader.util;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import org.daisy.reader.model.audio.AudioClip;
import org.daisy.reader.model.smil.AudioMediaObject;

public class AudioUtils {

	public static long framesToMillis(long frames, AudioFileFormat format) {		
		return framesToMillis(frames,format.getFormat().getFrameRate());		
	}

	public static long framesToMillis(long frames, float frameRate) {		
		return Math.round(1000.0 * frames / frameRate);		
	}
	
	public static double framesToSeconds(long frames, AudioFileFormat format) {		
		return frames / format.getFormat().getFrameRate();		
	}
	
	public static long millisToFrames(long ms, AudioFormat format) {
		return millisToFrames(ms, format.getFrameRate());
	}
	
	public static long millisToFrames(long ms, float frameRate) {
		return (long) (ms*frameRate/1000);
	}
	
	public static long secondsToFrames(double seconds, AudioFormat format) {
		return secondsToFrames(seconds, format.getFrameRate());
	}

	public static long secondsToFrames(double seconds, float frameRate) {
		return (long)(seconds*frameRate);
	}
	
	public static AudioClip toAudioClip(AudioMediaObject phrase, Long timeOffset) {
		Long end = timeOffset;
		if(end==null) end = Long.valueOf(phrase.getEndClock().millisecondsValue());
		return new AudioClip(phrase.getURL(),phrase.getStartClock().millisecondsValue(),end.longValue(),phrase);				
	}
}