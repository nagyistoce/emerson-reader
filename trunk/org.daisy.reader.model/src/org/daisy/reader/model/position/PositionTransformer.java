package org.daisy.reader.model.position;

import java.util.List;
import java.util.NoSuchElementException;

import org.daisy.reader.Activator;
import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.MediaObject;
import org.daisy.reader.model.smil.SmilFile;
import org.daisy.reader.model.smil.SmilSpine;
import org.daisy.reader.model.smil.TextMediaObject;
import org.daisy.reader.model.smil.TimeContainer;

/**
 * A transformation utility class for IPosition implementations.
 * Note - this class requires that a Model is currently loaded.
 * @author Markus Gylling.
 */
public class PositionTransformer {
	
	/**
	 * Transform an IPosition into a AudioMediaObject.
	 * @return A AudioMediaObject instance, or null if transformation failed.
	 */
	public static AudioMediaObject toSmilAudioMediaObject(
			IPosition position, SmilSpine spine) {
		//NOTE we need to return pointer instances here, not create a new object
		//because the AudioClipCursor uses pointer equivalency tests
		if(position instanceof SmilAudioPosition) {
			SmilAudioPosition smilPosition = (SmilAudioPosition) position;
			AudioMediaObject phrase = smilPosition.getSmilAudioMediaObject();			
			if(phrase!=null) {
				return phrase;
			}else{
				Activator.getDefault().logError("PositionTransformer toSmilAudioMediaObject clause not implemented", new IllegalStateException()); //$NON-NLS-1$				
			}
		}else{
			Activator.getDefault().logError("PositionTransformer toSmilAudioMediaObject outer clause not implemented", new IllegalStateException()); //$NON-NLS-1$
		}
				
		return null; 
	}
	
	/**
	 * Transform AudioMediaObject into a SmilAudioPosition.
	 * @return A SmilAudioPosition instance, or null if transformation failed.
	 */
	public static SmilAudioPosition toSmilPosition(
			AudioMediaObject audio) {		
		return new SmilAudioPosition(audio); 
	}
	
	/**
	 * Transform AudioMediaObject into a SmilAudioPosition.
	 * @return A SmilAudioPosition instance, or null if transformation failed.
	 */
	public static SmilAudioPosition toSmilPosition(
			AudioMediaObject audio, Long timeOffset) {
		if(timeOffset!=null) {
			return new SmilAudioPosition(audio, timeOffset);
		}	
		return new SmilAudioPosition(audio);
	}
	
	/**
	 * Transform an arbitrary IPosition into a SmilAudioPosition
	 * @return A SmilAudioPosition instance, or null if transformation failed.
	 */
	public static SmilAudioPosition toSmilPosition(
			IPosition position) {
		
		if(position instanceof SmilAudioPosition) {
			return (SmilAudioPosition) position;
			
		}else if(position instanceof URIPosition) {			
				URIPosition uriPosition = (URIPosition) position;
				//TODO if its a fragment only URI, use referer etc
				Model model = ModelManager.getModel();
				if(model!=null) {					
					SmilFile smilFile = ((SmilSpine)model.getSpine()).get(uriPosition.getFileName());
					if(smilFile != null) {
						String fragment = uriPosition.getFragment();
						if(fragment!=null) {
							Object d = smilFile.getDescendant(fragment);
							if(d!=null) {
								if(d instanceof AudioMediaObject) {
									return new SmilAudioPosition((AudioMediaObject)d);
								}else if(d instanceof TextMediaObject) {
									TextMediaObject text = (TextMediaObject)d;
									MediaObject phrase = text.getParentContainer().getFirst(AudioMediaObject.class);
									if(phrase!=null) {
										return new SmilAudioPosition((AudioMediaObject)phrase);	
									}									
								}else if(d instanceof TimeContainer) {
									TimeContainer tc = (TimeContainer)d;
									MediaObject phrase = tc.getFirst(AudioMediaObject.class);
									if(phrase!=null) {
										return new SmilAudioPosition((AudioMediaObject)phrase);	
									}									
								}
							}
						}							
						// no fragment
						AudioMediaObject phrase = smilFile.getFirstAudioMediaObject();
						if(phrase!=null) {
							return new SmilAudioPosition(phrase);
						}
						
					}else{
						System.err.println("SmilFile null in PositionTransformer.toSmilPosition(position)"); //$NON-NLS-1$
					}
				}
		}else if (position instanceof SerializableSmilAudioPosition) {
			SerializableSmilAudioPosition sap = (SerializableSmilAudioPosition) position;
			Model model = ModelManager.getModel();
			if(model!=null) {
				SmilFile f = ((SmilSpine)model.getSpine()).get(sap.getSmilFileName());
				if(f!=null) {
					AudioMediaObject phrase = null;				
					try{
						phrase = f.get(sap.phraseIndex);
					}catch (NoSuchElementException e) {
						phrase = f.getFirstAudioMediaObject();
					}
					return new SmilAudioPosition(phrase,sap.getTimeOffset());
				}								
			}
			System.err.println("toSmilPosition: could not convert serializable smilaudiopos"); //$NON-NLS-1$
		}else if(position instanceof TimePosition) {
			TimePosition tp = (TimePosition) position;
			long targetMillis = tp.getClock().millisecondsValue();
			
			SmilSpine spine = (SmilSpine)ModelManager.getModel().getSpine();
			long aggregatedSpineMillis = 0;
			
			SmilFile targetFile = null;		
			//TODO here we parse all SmilFiles that arent loaded yet, or unloaded
			//could use an iterator without statecheck
			for(SmilFile smilFile : spine) {
				aggregatedSpineMillis += smilFile.getDuration().millisecondsValue();
				if(aggregatedSpineMillis>targetMillis) {
					targetFile = smilFile;
					aggregatedSpineMillis -= smilFile.getDuration().millisecondsValue();
					break;
				}
			}
			
			if(targetFile!=null) {
				List<MediaObject> phraseList = targetFile.getRootContainer().getMediaChildren(AudioMediaObject.class);
				for(MediaObject mo : phraseList) {
					final AudioMediaObject amo = (AudioMediaObject)mo;
					aggregatedSpineMillis+=amo.getDuration();
					if(aggregatedSpineMillis>targetMillis) {
						return new SmilAudioPosition(amo);
					}
				}
				//hmm, didnt find an inline match, just return the smilfile
				final AudioMediaObject first = targetFile.getFirstAudioMediaObject();
				if(first!=null) return new SmilAudioPosition(first);
			}
			
			return null;
			
		}else{
			System.err.println("PositionTransformer toSmilPosition clause not implemented"); //$NON-NLS-1$
		}
		return null; 
	}

	public static SerializableSmilAudioPosition toSerializableSmilAudioPosition(IPosition position) {
		if(position instanceof SerializableSmilAudioPosition) 
			return (SerializableSmilAudioPosition) position;
		
		final SmilAudioPosition sap = toSmilPosition(position);
		
		final AudioMediaObject phrase = sap.getSmilAudioMediaObject();
		
		final SmilFile smil = phrase.getParentContainer().getParentFile();
				
		return new SerializableSmilAudioPosition(smil.indexOf(phrase),smil.getLocalName(),sap.getTimeOffset());
		
	}
	
	/**
	 * Create an IAutonomousPosition. This method requires that the Model instance
	 * to which the given IPosition refers is active in the ModelManager.
	 */
	public static IAutonomousPosition toAutonomousPosition(IPosition position) {
		if(position instanceof URIPosition) return (URIPosition) position;
		if(position instanceof SerializableSmilAudioPosition) return (SerializableSmilAudioPosition) position;
		if(position instanceof SmilAudioPosition) return toSerializableSmilAudioPosition(position);
		String err = "toAutonomousPosition not implemented for " + position.getClass().getName(); //$NON-NLS-1$
		System.err.println(err);
		Activator.getDefault().logError(err, new IllegalStateException());
		return null;
	}

	/**
	 * Transform an arbitrary IPosition into a TimePosition
	 * @return A TimePosition instance, or null if transformation failed.
	 */
	public static TimePosition toTimePosition(IPosition position) {
		if(position instanceof TimePosition) 
			return (TimePosition) position;
		
		SmilAudioPosition sap = PositionTransformer.toSmilPosition(position);
		if(sap!=null) {
			SmilSpine spine = (SmilSpine)ModelManager.getModel().getSpine();
			
			long aggregatedSpineMillis = 0;
			
			SmilFile targetFile = null;		
			//TODO here we parse all SmilFiles that arent loaded yet, or unloaded
			//could use an iterator without statecheck
			for(SmilFile smilFile : spine) {
				aggregatedSpineMillis += smilFile.getDuration().millisecondsValue();
				if(smilFile==sap.getSmilFile()) {
					targetFile = smilFile;
					aggregatedSpineMillis -= smilFile.getDuration().millisecondsValue();
					break;
				}
			}
			
			long aggregatedInlineMillis = 0;			
			
			if(targetFile!=null) {
				AudioMediaObject targetPhrase = sap.getSmilAudioMediaObject();
				List<MediaObject> phraseList = targetFile.getRootContainer().getMediaChildren(AudioMediaObject.class);
				for(MediaObject mo : phraseList) {
					AudioMediaObject amo = (AudioMediaObject)mo;					
					if(amo == targetPhrase) {
						return new TimePosition(aggregatedSpineMillis+aggregatedInlineMillis);
					}
					aggregatedInlineMillis+=amo.getDuration();
				}
				//hmm, didnt find an inline match, just return the smilfiles time
				return new TimePosition(aggregatedSpineMillis);
			}			
		}
		return null;
	}
	
}
