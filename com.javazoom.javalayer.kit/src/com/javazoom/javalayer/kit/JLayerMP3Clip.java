package com.javazoom.javalayer.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import org.daisy.reader.model.audio.AudioClip;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.util.AudioUtils;


public class JLayerMP3Clip extends AudioClip {
	/** The MPEG audio decoder. */
	Decoder decoder;	
	/** The AudioDevice the audio samples are written to. */
	AudioDevice device;	
	/** The framerate of this clip */
	float frameRate; 	
	/** Start frame of this clip */
	long startFrame = 0;
	/** End frame of this clip */
	long endFrame = Integer.MAX_VALUE;
	
	public JLayerMP3Clip(AudioClip clip) throws JavaLayerException {
		this(clip.getUrl(),clip.getStartMillis(),clip.getEndMillis(),clip.getSmilAudioMediaObject());
	}
	
	public JLayerMP3Clip(URL url, long startMillis, long endMillis,AudioMediaObject phrase) throws JavaLayerException {
		
		super(url, startMillis, endMillis, phrase);
		InputStream is = null;
		Bitstream bs = null;
		try{
			//create a Device and a Decoder
			device = FactoryRegistry.systemRegistry().createAudioDevice();
			device.open(decoder = new Decoder());
			//System.err.println("CREATING DEVICE " + device.hashCode());
			//calculate framerate, startfIrame, endframe
			//is = URLUtils.decode(url).openStream();
			is = url.openStream();
			bs = new Bitstream(is);		
			Header h = bs.readFrame();
			frameRate = (float) ((1.0 / (h.ms_per_frame())) * 1000.0);		
		    startFrame = AudioUtils.millisToFrames(startMillis, frameRate);
		    endFrame = AudioUtils.millisToFrames(endMillis, frameRate);
		}catch (Exception e) {
			if(e instanceof JavaLayerException) throw (JavaLayerException)e;
			throw new JavaLayerException(e.getMessage(),e);
		}finally{
			try {
				if(bs!=null) bs.close();
				if(is!=null) is.close();
			} catch (IOException e) {
					
			}
		}
	}
	
}
