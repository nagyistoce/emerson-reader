//package org.daisy.reader.model.smil;
//
//import org.daisy.reader.model.position.SmilAudioPosition;
//
//public class SmilUnloaderDaemon implements ISmilPositionChangeListener {
//	private SmilFile currentSmilFile;
//	private SmilSpine spine;
//	
//	public SmilUnloaderDaemon(SmilSpine spine) {
//		this.spine = spine;
//		spine.getAudioClipCursor().addPositionChangeListener(this);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.daisy.reader.model.smil.ISmilPositionChangeListener#positionChanged(org.daisy.reader.model.position.SmilAudioPosition)
//	 */
//	public void positionChanged(SmilAudioPosition newPosition) {
//		SmilFile incomingSmilFile = newPosition.getSmilFile();						
//		if(currentSmilFile!=null && incomingSmilFile!=currentSmilFile) {
//			currentSmilFile.unload();
//		}				
//		currentSmilFile = incomingSmilFile;		
//	}
//
//	public void close() {
//		if(spine!=null && spine.getAudioClipCursor()!=null)
//			spine.getAudioClipCursor().removePositionChangeListener(this);
//		spine=null;
//		currentSmilFile = null;		
//	}
//}
