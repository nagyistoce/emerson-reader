package org.daisy.reader.model.smil;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.daisy.reader.Activator;
import org.daisy.reader.util.URIStringParser;

public class SmilFile {

	/** The local name of the SMIL file with no path (relative or absolute) */
	private String localName;
	
	/** The absolute URL of this SMIL file */
	final URL url;
				
	/** An ISmilLoader instance for later lazy parse of this SmilFile */
	private ISmilLoader loader;

	/** The SmilSpine instance in which this SmilFile lives */
	private SmilSpine spine;
	
	/** The lifecycle state of this SmilFile */
	private State state;

	/** The root seq timecontainer of this SmilFile */
	private SeqContainer root;
	
	/** A flattened view of the audio media object children of this smil file */
	private List<MediaObject> flatAudioList; //TODO make this redundant
	
	private SmilClock mDuration;

	/** Lifecycle states of this SmilFile */
	enum State {
		UNPARSED,
		PARSED_OK,
		PARSED_ERROR;
	}
	
	/**
	 * Constructor. Initializes the object, but does not
	 * parse the physical SMIL file.
	 * @param url An absolute resolving URL to this SMIL file 
	 * @param loader An ISmilLoader instance for later lazy parse of this SmilFile
	 * @param spine The SmilSpine instance in which this SmilFile lives.
	 */
	public SmilFile(URL url, ISmilLoader loader, SmilSpine spine) {
		this.url = url;		
		this.loader = loader;	
		this.spine = spine;
		this.state = SmilFile.State.UNPARSED;
	}
	
	public SeqContainer getRootContainer() {
		return root;
	}
	
	public void setRootContainer(SeqContainer root) {
		this.root = root;
	}
	
	public SmilClock getDuration() {
		if(mDuration==null) {
			try{
				mDuration = SmilUtils.calcDuration(getFlatAudioList());			
			}catch (Exception e) {
				Activator.getDefault().logError(e.getMessage(), e);
				mDuration = null;
			}
		}
		return mDuration;
	}
	
	public void setDuration(String clockValue) {
		try{
			mDuration = new SmilClock(clockValue);
		}catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
			mDuration = null;
		}
	}
	
	/**
	 * Get the localname of this smil file. The name uses the syntax of a <strong>decoded</strong> URI string.
	 * @return
	 */
	public String getLocalName() {
		if(localName==null) {
			try {
				localName = URIStringParser.getFileLocalName(url.toURI().getPath());
			} catch (URISyntaxException e) {
				localName = URIStringParser.getFileLocalName(url.getPath());				
			}
		}
		return localName;
	}
	
	/**
	 * Get the absolute URL of this SmilFile.
	 */
	public URL getURL() {
		return url;
	}
	

	public SmilFile.State getState() {
		return state;
	}
	
	public SmilSpine getSpine() {
		return spine;
	}
	
	/**
	 * Load/parse this SmilFile and populate its data fields.
	 * @return The State after the parse attempt.
	 */
	SmilFile.State load() {

		if(this.state!=SmilFile.State.UNPARSED) 
			throw new IllegalStateException("already parsed"); //$NON-NLS-1$
				
		if(loader.load(url, this)) {
			state = SmilFile.State.PARSED_OK;
		}else{
			state = SmilFile.State.PARSED_ERROR;
		}
		return state;
	}
	
	/**
	 * Unload this SMIL files data fields.
	 */
	void unload() {
		root = null; //TODO explicit clear of children?
		flatAudioList = null;
		state = SmilFile.State.UNPARSED;
	}
	
	/**
	 * Get the index of given audio object/element within this SMIL file.
	 * If param is the first audio object/element in the SMIL file, index will be 0.
	 * @return 0 or higher if the given object is found, else -1.
	 */
	public int indexOf(AudioMediaObject audio) {
		if(flatAudioList==null) {
			flatAudioList = root.getMediaChildren(AudioMediaObject.class);
		}
		return flatAudioList.indexOf(audio);
	}
	
	List<MediaObject> getFlatAudioList() {
		if(flatAudioList==null) {
			flatAudioList = root.getMediaChildren(AudioMediaObject.class);
		}
		return flatAudioList;
	}
	
	/**
	 * Get the AudioMediaObject at the given index position in a 
	 * sequential view of the SmilAudioMediaObjects in this SmilFile. 
	 * @return AudioMediaObject
	 * @throws NoSuchElementException
	 */
	public AudioMediaObject get(int index) {
		if(flatAudioList==null) {
			flatAudioList = root.getMediaChildren(AudioMediaObject.class);
		}
		
		try{
			return (AudioMediaObject) flatAudioList.get(index);
		}catch (IndexOutOfBoundsException e) {
			
		}
		
		throw new NoSuchElementException();
	}
	
	
	/**
	 * Retrieve the first audio clip in this SmilFile,
	 * or null if it contains no audio, or null if retrieval 
	 * failed.
	 */
	public AudioMediaObject getFirstAudioMediaObject() {
		if(flatAudioList==null) {
			flatAudioList = root.getMediaChildren(AudioMediaObject.class);
		}
		if(flatAudioList.isEmpty()) return null;
		return (AudioMediaObject)flatAudioList.get(0);
	}
	
	/**
	 * Return a descendant within this SmilFile which
	 * carries the given id.
	 * @return A descendant, or null if no entity with the
	 * given id exists
	 */
	public Object getDescendant(String id) {
		if(root!=null) {			
			if(root.getID()!=null && root.getID().equals(id)) return root;
			return root.getDescendant(id);			
		}
		return null;
	}
	
}
