package org.daisy.reader.model.d202;

import java.net.URL;
import java.util.Iterator;

import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.TimeContainer;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.ncx.NcxItem;

/**
 * A subclass of the default Ncx INavigation impl that optimizes the 
 * execution of some  methods.
 * @author Markus Gylling
 */
public class D202Ncx extends Ncx {
	private TimeContainer lastTimeContainer = null;
	
	public D202Ncx(URL ncc) {
		super();
		super.ncxURL = ncc;		
	}
	
		
	@Override
	public void positionChanged(ModelPositionChangeEvent event) {
		/*
		 * Override to avoid unecessary iterations.
		 * Only update if the parent time container changed.
		 */
		IPosition position = event.getNewPosition();
		if(position instanceof SmilAudioPosition) {			
			TimeContainer parentContainer = 
				((SmilAudioPosition)position).getSmilAudioMediaObject().getParentContainer();
			if(parentContainer == lastTimeContainer) return;
			lastTimeContainer = parentContainer;
		}		
		super.positionChanged(event);
	}
	
	@Override
	protected NcxItem getNcxItem(Iterator<?> iterator, AudioMediaObject phrase) {
		NcxItem navItem = null;
		
		/*
		 * In 2.02 the NCC points to the audios parentparent par, or the audios
		 * parentparentpar text child  		
		 */		
		final String targetFileName = phrase.getParentContainer().getParentFile().getLocalName();
		TimeContainer parentContainer = phrase.getParentContainer();
		if(parentContainer.getParentContainer()!=null) {
			//this should be the par TODO check if variations
			parentContainer = parentContainer.getParentContainer();
		}
		
		while(iterator.hasNext()) {
			navItem = (NcxItem) iterator.next();
			//TODO should use relative URI instead of filelocalname
			if(targetFileName.equals(navItem.getTargetFileLocalName())) {
				final String target = navItem.getTargetFragment();
				if((parentContainer.getID()!=null && parentContainer.getID().equals(target))
						|| parentContainer.getDescendant(target)!=null) {
					return navItem;
				}
			} 
		}
		
		return null;
	}
}
