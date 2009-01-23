package org.daisy.reader.model.dtb;

import java.net.URL;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.audio.AudioClipFeeder;
import org.daisy.reader.model.audio.AudioClipPlayer;
import org.daisy.reader.model.audio.AudioKitFactoryFactory;
import org.daisy.reader.model.audio.BlockingAudioClipQueue;
import org.daisy.reader.model.audio.IAudioKitFactory;
import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.exception.UnsupportedContentTypeException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.PositionTransformer;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.smil.AudioClipCursor;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.SmilSpine;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;
import org.daisy.reader.util.AudioUtils;
import org.daisy.reader.util.FifoStack;

/**
 * An abstract DTB Model. 
 * @author Markus Gylling
 */
public abstract class DtbModel extends Model {

	protected SmilSpine spine;
	protected FifoStack<SmilAudioPosition> positionHistory;
	protected AudioClipPlayer player;
	protected AudioClipFeeder feeder;
	protected IAudioKitFactory cachedAudioKitFactory;
	protected AudioKitFactoryFactory audioKitFactoryFactory;
				
	public DtbModel(URL url, INavigation navigation, Metadata metadata, SmilSpine spine) {
		super(url, navigation, metadata);
		this.spine = spine;
		this.spine.setModel(this);
		positionHistory = new FifoStack<SmilAudioPosition>(10);			
	}

	@Override
	public SmilAudioPosition getPosition() {
		if(getCurrentState()== ModelState.STOPPED) {
			//added this so that we increase chances of getting a millis comparator
			//note that the stop impl stores a history mark
			SmilAudioPosition peek = positionHistory.peek();
			if(peek==null) throw new IllegalStateException();
			return peek;
		}
		
		//Create a SMIL position based on the SmilSpines current audio phrase
		SmilAudioPosition position = PositionTransformer.toSmilPosition( 
			spine.getAudioClipCursor().current());
		
		//if, playing add the offset within the audio file
		if(player!=null && !player.isDisposed()) {			
			position.setTimeOffset(Long.valueOf(player.getCurrentTime()));
		}	
		
		return position;
	}

	private boolean firstSetPosCall = true;
	@Override
	public boolean setPosition(IPosition position) {
		//System.err.println("DTBModel#setpostion(IPosition) " +position.toString());
		boolean result = false;		
		//a position can be set as long as we are not disposed
		if(!this.isDisposed()) {	
			
			//register our previous position in history
			//unless this is the very first call
			//in which case the first smil might be 
			//loaded unnecessarily			
			if(firstSetPosCall) {
				firstSetPosCall = false;								
			}else{
				positionHistory.push(getPosition());
			}
			
			
			//store the Model state previous to relocation
			ModelState preState = this.getCurrentState();
			
			//register that relocation is commencing
			fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.RELOCATING));
			
			//make sure we have a SmilAudioPosition 
			SmilAudioPosition targetPosition = PositionTransformer.toSmilPosition(position);
			
			if(targetPosition!=null) {
				//make sure we have a AudioMediaObject
				AudioMediaObject targetPhrase = PositionTransformer.toSmilAudioMediaObject(targetPosition, spine);
				
				//register the new position in SmilSpine AudioClipCursor
				//and in the local positionHistory			
				if(targetPhrase!=null) {
					try{
						//relocate the SmilSpines AudioClip cursor						
						spine.getAudioClipCursor().set(targetPhrase);
						//register in local history
						positionHistory.push(targetPosition);
						//register that relocation is done
						fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.RELOCATED));
						result = true;
					}catch (IllegalArgumentException e) {
						Activator.getDefault().logError(e.getMessage(), e);
						fireStateChangeEvent(new ModelStateChangeEvent(this,preState));
						return result;
					}

					render(targetPhrase,targetPosition.getTimeOffset());

				}else{
					fireStateChangeEvent(new ModelStateChangeEvent(this,preState));
				}
			}else{
				fireStateChangeEvent(new ModelStateChangeEvent(this,preState));
			}
		} //if(!this.isDisposed()) 			
		return result;

	}

	/**
	 * Start rendering (playing) from an unspecified position.
	 * This means that the concrete instance should pick a rendering
	 * position based on it last recorded position.
	 * @see #render(IPosition)
	 * @return true if rendering started, false otherwise
	 */	
	public boolean render() {
		//System.err.println("DtbModel#render");
		/*
		 * Find out where to start from, and call
		 * the worker method
		 */		
		AudioMediaObject phrase = null;
		Long offset = null;
		
		if(!positionHistory.isEmpty()) {
			SmilAudioPosition pos = positionHistory.peek();
			phrase = PositionTransformer.toSmilAudioMediaObject(pos, spine);
			offset = pos.getTimeOffset();
		}else{
			//no recorded previous positions in this session. 
			//Most probably means that we are in the beginning
			//of a new book. Let the AudioClipCursor decide.
			phrase = spine.getAudioClipCursor().current();
			if(phrase==null) {
				spine.getAudioClipCursor().set(spine.getFirstAudioMediaObject());	
				phrase = spine.getAudioClipCursor().current();
			}	
		}
		
		if(phrase!=null)
			return render(phrase, offset);
		return false;
	}
	
	/**
	 * The main worker method for playback.
	 * <p>Start playing from incoming position, and
	 * continue phrase by phrase until an event 
	 * (pause, dispose, navigate, end of book, etc) 
	 * occurs.</p>
	 * 
	 * <p>This method can be called while playback
	 * is ongoing, or while idle</p>
	 * 
	 * @param phrase The AudioMediaObject to start at
	 * @param timeOffset. A timeoffset in the audio resource
	 * referenced by phrase, which should be the starting point
	 * of the rendering, counting from the start of the audio 
	 * resource. May be null, in which case the start 
	 * frame inherent to phrase will be the start time.
	 * @throws UnsupportedContentTypeException If the 
	 * audio in the given phrase is of an unsupported type
	 */	
	private boolean render(AudioMediaObject phrase, Long timeOffset) {		
		
		if(feeder!=null || player!=null) stop();
				
		BlockingAudioClipQueue queue = new BlockingAudioClipQueue(
				AudioUtils.toAudioClip(phrase,timeOffset));
		
		IAudioKitFactory factory = null;
		try {
			factory = getAudioKitFactory(phrase.getURL());
		} catch (UnsupportedContentTypeException e) {
			//TODO bubble this up
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			return false;
		}						
		
		player = factory.newClipPlayer(queue);
		feeder = factory.newClipFeeder(queue);
								
		feeder.start();		
		fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.READING));
		player.start();
		//TODO catch uncaught exception from threads, if playback fails we need to know
		return true;
	}
			
	/**
	 * Stop rendering (playing), and maintain positional information.
	 * If the Model is not currently rendering, this call has no effect. 
	 * @return true if an ongoing rendering stopped, false otherwise
	 */
	public boolean stop() {	
		//System.err.println("DtbModel#stop");
		if(getCurrentState()!= ModelState.STOPPED) {			
			fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.STOPPING));				
			//record the last position in the history stack
			positionHistory.push(getPosition());
		}	
		
		//kill the player and feeder threads		
		if(player!=null) {
			player.interrupt();
				player.close();
					player = null;
		}
		
		if(feeder != null) {
			feeder.interrupt();
				feeder.close();
					feeder = null;
		}
							
		if(getCurrentState()== ModelState.STOPPING) {
			fireStateChangeEvent(new ModelStateChangeEvent(this,ModelState.STOPPED));			
			return true;
		}
		
		return false;			
	}

	@Override
	protected void doDispose() {
		if(getCurrentState()!= ModelState.STOPPED 
				&& getCurrentState()!= ModelState.STOPPING)
			stop();
		this.positionHistory.clear();
		this.spine = null;
		this.cachedAudioKitFactory = null;
		this.audioKitFactoryFactory = null;	
	}

	@Override
	public IPosition getAdjacentPosition(Direction direction, Semantic semantic) {
		if(semantic == Semantic.PHRASE) {
			AudioClipCursor cursor = spine.getAudioClipCursor();
			AudioMediaObject phrase = null;
			if(direction == Direction.NEXT) {
				phrase = cursor.peekNext();
				if(phrase!=null)
					return PositionTransformer.toSmilPosition(phrase);
			}else if(direction == Direction.PREV) {
				phrase = cursor.peekPrevious();
				if(phrase!=null) {
					return PositionTransformer.toSmilPosition(phrase);
				}else if(cursor.current() == spine.getFirstAudioMediaObject()) {
					return PositionTransformer.toSmilPosition(cursor.current());
				}
			}
		}
		//else delegate to INavigation
		return getNavigation().getAdjacentPosition(direction, semantic);		
	}

	@Override
	public SmilSpine getSpine() {
		return spine;
	}

	@Override
	public boolean supportsNavigationMode(Semantic semantic) {
		return(semantic==Semantic.PHRASE) 
			? true
			: this.navigation.getFirst(semantic)!=null;
	}
		
	protected IAudioKitFactory getAudioKitFactory(URL audio) throws UnsupportedContentTypeException {
		IAudioKitFactory factory = null;
		
		if(cachedAudioKitFactory!=null 
				&& cachedAudioKitFactory.supportsContentType(audio)) {			
			factory = cachedAudioKitFactory;
		}else{
			if(audioKitFactoryFactory==null)
				audioKitFactoryFactory = AudioKitFactoryFactory.newInstance();			
			factory = audioKitFactoryFactory.newAudioKitFactory(audio);			
			cachedAudioKitFactory = factory;			
		}		
		return factory;
	}

	/**
	 * Convenience method.
	 * @return true if this Models current state is PLAY_PREPARING
	 * or PLAYING.
	 */
	public boolean isRendering() {
		ModelState state = getCurrentState();
		return  state == ModelState.READ_PREPARING ||
			state== ModelState.READING;
	}

	@Override
	public Object getProperty(String key) throws PropertyException {
		if(IPropertyConstants.PUBLICATION_DURATON.equals(key)) {
			return spine.getDuration();
		}
		return super.getProperty(key);
	}

}
