package org.daisy.reader.model.smil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.daisy.reader.model.semantic.Semantic;

public abstract class TimeContainer {

	private TimeContainer parentContainer;
	private SmilFile parentFile;
	private List<TimeContainer> childContainers;
	private Map<Class<? extends MediaObject>, List<MediaObject>> childMedia;
	private String id;
	private Semantic semantic;

	/**
	 * Constructor.
	 * @param id The ID of this container in the XML representation.
	 * @param parentContainer May be null of this is a root container
	 * @param file. Is never null.
	 */
	public TimeContainer(String id, TimeContainer parentContainer, SmilFile file) {
		this.id = id;
		this.parentContainer = parentContainer;
		this.parentFile = file;		
		this.childMedia = new HashMap<Class<? extends MediaObject>, List<MediaObject>>();
	}
	
	public void setSemantic(Semantic sem) {
		this.semantic = sem;		
	}
	
	/**
	 * Retrieve a stated semantic of this time container, or
	 * null if no semantic has been set.
	 */
	public Semantic getSemantic() {
		return this.semantic;		
	}
	
	/**
	 * Retrieve the parent of this container. May be null if this is a root container 
	 * (which should only happen in DAISY if this is a seq container)
	 */
	public TimeContainer getParentContainer() {
		return parentContainer;
	}

	/**
	 * Retrieve the XML ID of this time container. The return value is not interned.
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Retrieve the child containers of this time container. If this
	 * container does not have any container children, return null.
	 */
	public List<TimeContainer> getChildContainers() {
		return childContainers;
	}
	
	/**
	 * Retrieve the media children of the given type within this time container. If this
	 * container does not have any media children of the given type, return an empty list. 
	 * <p>Media children of descendant time containers are included.</p>
	 */
	public List<MediaObject> getMediaChildren(Class<? extends MediaObject> type) {
		List<MediaObject> ret = new LinkedList<MediaObject>();
		if(childMedia.get(type)!=null) {
			ret.addAll(childMedia.get(type));
		}
		if(childContainers!=null) {
			for(TimeContainer tc : childContainers) {
				ret.addAll(tc.getMediaChildren(type));
			}
		}	
		return ret;
	}
		
	public void addChildContainer(TimeContainer tc) {
		if(childContainers == null) {
			childContainers = new ArrayList<TimeContainer>();
		}	
		childContainers.add(tc);
	}

	public void addMediaObject(MediaObject mediaObject) {
		List<MediaObject> mediaList = childMedia.get(mediaObject.getClass());
		if(mediaList==null) {
			mediaList = new ArrayList<MediaObject>();
			childMedia.put(mediaObject.getClass(), mediaList);
		}
		mediaList.add(mediaObject);
		
	}
		
	/**
	 * Retrieve the SmilFile in which this container lives. Is never null.
	 */
	public SmilFile getParentFile() {
		return parentFile;
	}
	
	/**
	 * Return a descendant element in this time container
	 * which carries the given XML ID, or null if no
	 * descendant with that ID exists.
	 * @return
	 */
	public Object getDescendant(String id) {
				
		//first iterate over direct childmedia,
		for(List<MediaObject> list : childMedia.values()) {
			for(MediaObject smo : list) {
				if(smo.id!=null && smo.id.equals(id)) {
					return smo;
				}
			}
		}
		
		//then child time containers
		if(childContainers!=null) {
			for(TimeContainer tc : childContainers) {
				if(tc.getID()!=null && tc.getID().equals(id)) return tc;
				Object o = tc.getDescendant(id);
				if(o!=null) return o;
			}
		}

		return null;
	}
	
	/**
	 * Convenience method. Retrieve the first media object of 
	 * given type within this container, or null if no media 
	 * objects of given type exist.
	 */
	public MediaObject getFirst(Class<? extends MediaObject> type) {
		List<MediaObject> list = getMediaChildren(type);
		return (!list.isEmpty()) ? list.get(0) : null;		
	}
	
}
