package org.daisy.reader.model.ops;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.URIPosition;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.state.ModelState;
import org.daisy.reader.model.state.ModelStateChangeEvent;

/**
 * A model for OPS 2.0. Note that the default ModelProvider for this model
 * utilizes a temporary directory to store the epub contents.
 * @author Markus Gylling
 */
public class OpsModel extends Model {
	
	private File tempdir;
	private List<PackageFileItem> spine;
	
	/** For epubs, positional information consists
	 * of a URIPosition which refers to an
	 * entry of the spine.
	 */
	private URIPosition currentPosition;
	
	OpsModel(URL epub, List<PackageFileItem> spine, INavigation navigation, Metadata metadata, File tempdir) {
		super(epub, navigation, metadata);
		this.spine = spine;
		this.tempdir = tempdir;
		//since getPosition must never return null, set currentPosition to the very start
		try {						
			this.currentPosition = validate(new URIPosition(this.spine.get(0).mHref,new File(tempdir,"dummy.opf").toURI().toURL())); //$NON-NLS-1$
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		}
	}
	
	
	@Override
	public URIPosition getPosition() {		
		return this.currentPosition;
	}

	@Override
	public boolean setPosition(IPosition position) {
				
		if(position==null || this.isDisposed() 
				|| !(position instanceof URIPosition)) return false;
		
		fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.RELOCATING));
						
		URIPosition validated = validate((URIPosition)position);
		
		if(validated==null) return false;				
		
		currentPosition = validated;
		
		fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.RELOCATED));
		
		firePositionChangeEvent(currentPosition);
		
		return true;
	}
	
	/**
	 * Return a validated URIPosition or null of the position is not resolvable (excluding fragment checks).
	 * @param candidate
	 * @return a URIPosition to broadcast if the position is valid, else null.
	 */
	private URIPosition validate(URIPosition candidate) {
		try {			
			String candidatePath = candidate.getAbsoluteURI().toURL().getPath();						
			for(PackageFileItem item : this.getSpine()) {	
				String itemPath = item.mItemURL.getPath();
				if(itemPath.equals(candidatePath)) {
					return candidate;
				}
			}
		} catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(),e);
		}		
		return null;
	}

	@Override
	public List<PackageFileItem> getSpine() {		
		return spine;
	}

	@Override
	protected void doDispose() {
				
		//delete extracted members if we are in tempdir
		File[] files = tempdir.listFiles();
		if(files!=null) {
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				f.delete();
			}
		}
		tempdir.delete();
	}

	@Override
	public IPosition getAdjacentPosition(Direction direction, Semantic semantic) {
		//for epubs, we delegate directly to INavigation
		return navigation.getAdjacentPosition(direction, semantic);
	}

	@Override
	public void setProperty(String key, Object value) throws PropertyException {
			throw new PropertyException(key);
	}

	public Object getProperty(String key) throws PropertyException {
		if(IPropertyConstants.PUBLICATION_TYPE.equals(key))
			return new PublicationType(Messages.OpsModel_epub, "OPS 2.0",this.getClass()); //$NON-NLS-1$
		return super.getProperty(key);
	}
	
	@Override
	public boolean supportsNavigationMode(Semantic semantic) {		
		return navigation.getFirst(semantic)!=null;
	}

	@Override
	public boolean render() {
		//nothing to do here
		if(!this.isDisposed()) {
			fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.READ_PREPARING));
			fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.READING));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean stop() {
		//nothing to do here
		if(!this.isDisposed()) {
			fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.STOPPING));
			fireStateChangeEvent(new ModelStateChangeEvent(this, ModelState.STOPPED));
			return true;
		}
		return false;
		
	}

}
