package org.daisy.reader.model.smil;

import java.net.MalformedURLException;
import java.net.URL;

import org.daisy.reader.Activator;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.util.URLUtils;

public class MediaObject {
	/** A not interned id */
	String id;
	String src;
	TimeContainer parent;
	Semantic semantic;	
	private URL url;
	
	public MediaObject(String id, String src, TimeContainer parent) {
		this.id = id;
		//if(this.id!=null)this.id.intern();
		this.src = src;
		this.parent = parent;
	}
	
	public TimeContainer getParentContainer() {
		return parent;
	}
	
	/**
	 * Retrieve the XML ID of this media object. The return value is not interned.
	 */
	public String getID() {
		return id;
	}
	
	public String getSrc() {
		return src;
	}
	
	public void setSemantic(Semantic sem) {
		this.semantic = sem;		
	}
	
	/**
	 * Retrieve a stated semantic of this time container, or
	 * null if no semantic has been set.
	 */
	public Semantic getSemantic() {
		return getSemantic(false);	
	}
	
	/**
	 * Retrieve a stated semantic of this time container, or
	 * null if no semantic has been set.
	 * @param whether Semantic on time container parents 
	 * should be included
	 */
	public Semantic getSemantic(boolean considerParents) {
		if(!considerParents)return this.semantic;	
		
		TimeContainer tc = this.getParentContainer();		
		while(tc!=null) {
			if(tc.getSemantic()!=null) return tc.getSemantic();
			tc = tc.getParentContainer();
		}
		
		return null;
	}
	
	/**
	 * Get an absolute URL to the resource that this MediaObject
	 * refers to. This method does not guarantee that the returned URL resolves .
	 * @returns a URL to an audio resource, or null of there was an error retrieving
	 * the resource. 
	 */
	public URL getURL() {
		if(url==null) {
			try {				
				url = URLUtils.resolve(getParentContainer().getParentFile().getURL(), src);
			} catch (MalformedURLException e) {
				Activator.getDefault().logError(e.getMessage(), e);
				return null;
			}
		}
		return url;	
	}
}
