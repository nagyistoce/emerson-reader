package com.javazoom.javalayer.kit;

import java.io.InputStream;
import java.net.URL;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;

import org.daisy.reader.model.audio.AudioClip;
import org.daisy.reader.model.audio.AudioClipPlayer;
import org.daisy.reader.model.audio.AudioException;
import org.daisy.reader.model.audio.BlockingAudioClipQueue;
import org.daisy.reader.util.AudioUtils;

public final class JLayerMP3Player extends AudioClipPlayer {

	/** URL of currently registered audio resource */
	private URL currentURL;
		
	/** Frame rate (frames per second) of the current audio resource */
	private float frameRate;
	
	/** InputStream of currently registered audio resource */
	private InputStream currentInputStream;
	
	/** Bitstream of currently registered audio resource */
	private Bitstream currentBitstream;

	/** The last rendered frame, counting from start of current audio resource */
	private long currentFrame = 0;
	
	/** The MPEG audio decoder. */
	private Decoder decoder;
	
	/** The AudioDevice the audio samples are written to. */
	private AudioDevice device;

	/** Start frame */
	private long startFrame = 0;

	/** End frame */
	private long endFrame = Integer.MAX_VALUE;
	
	public JLayerMP3Player(BlockingAudioClipQueue queue) {
		super(queue);
	}
	
	@Override
	protected void set(AudioClip clip) throws AudioException {		
				
		try{
			JLayerMP3Clip jclip = null;
			try{
				jclip = (JLayerMP3Clip) clip;
			}catch (Exception e) {
				jclip = new JLayerMP3Clip(clip);
			}	
			//if the new clip is in a new audio file,
			//or if its start frame is before our currentFrame position,
			//we need to reinitialize.
			if(currentURL==null 
					|| !jclip.getUrl().getPath().equals(currentURL.getPath()) 
						|| jclip.startFrame < currentFrame) {
				closeStreams();
				//dispose()
				device = jclip.device;
				decoder = jclip.decoder;
				currentURL = jclip.getUrl();
				currentFrame = 0;
				frameRate = jclip.frameRate;
				currentInputStream = jclip.getUrl().openStream();
				currentBitstream = new Bitstream(currentInputStream);
			}else{
				//System.err.println("CLOSING DEVICE " + jclip.device.hashCode());
				jclip.device.close();
			}
			startFrame = jclip.startFrame;
			endFrame = jclip.endFrame;	
			alignBitStream(startFrame);
		}catch (Exception e) {
			dispose();
			throw new AudioException(e.getMessage(),e);
		}
	}

	@Override
	protected void play() {
        
		long frames = endFrame - startFrame;
		boolean remaining = true;
				
		while (frames-- > 0  && remaining  && !isDisposed()) {									
			try {
				remaining = decodeFrame();
			} catch (JavaLayerException e) {
				Activator.getDefault().logError(e.getMessage(), e);
			}						
		}
		
		if(currentFrame == endFrame) {
			// last frame, ensure all data flushed to the audio device.
			if(device!=null) {
				device.flush();						
			}
		}		
		
		if(isDisposed() && device!=null)
			device.close();
			
	}
	
	@Override
	protected void dispose() {
		try {
			if(device!=null) {
				//System.err.println("CLOSING DEVICE " + device.hashCode());
				device.close();				
			}
			closeStreams();
		} catch (Throwable t) {
			Activator.getDefault().logError(t.getMessage(), t);
		}	
		
	}

	/**
	 * Make sure the bitstream is aligned at start frame
	 */
	private void alignBitStream(long startFrame) {
		
		if(startFrame > currentFrame) {
			//fastforward
			boolean ret = true;
			long offset = startFrame-currentFrame;
			while (offset-- > 0 && ret) {
				try {
					ret = skipFrame();
				} catch (JavaLayerException e) {
					Activator.getDefault().logError(e.getMessage(), e);
				}
			}	
		}
	}
	
	/**
	 * Skips over a single frame
	 * @return false if there are no more frames to decode, true otherwise.
	 */
	private boolean skipFrame() throws JavaLayerException {
		Header h = currentBitstream.readFrame();
		if (h == null) return false;
		currentBitstream.closeFrame();		
		currentFrame++;
		return true;
	}
	
	@Override
	public Long getCurrentTime() {
		return AudioUtils.framesToMillis(currentFrame, frameRate);
	}
	
	/**
	 * Decode a single frame.
	 * @return true if there are no more frames to decode, false otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException {
		try {
			AudioDevice out = device;
			if (out == null) return false;

			Header h = currentBitstream.readFrame();
			if (h == null) return false;
			
			// sample buffer set when decoder constructed
			
			SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, currentBitstream);
			
			synchronized (this) {
				out = device;				
				if(out != null) {
					try{
						out.write(output.getBuffer(), 0, output.getBufferLength());						
					}catch (JavaLayerException e) {						
						throw e;
					}	
				}
			}
			currentBitstream.closeFrame();
			currentFrame++;

		} catch (RuntimeException ex) {
			//TODO may just log error
			throw new JavaLayerException(ex.getMessage(), ex);
		}
		
		return true;
	}

	
	private void closeStreams() {
		try {
			if (currentBitstream != null)
				currentBitstream.close();
			if (currentInputStream != null)
				currentInputStream.close();
		} catch (Exception e) {
	
		}
	}
	
	
}