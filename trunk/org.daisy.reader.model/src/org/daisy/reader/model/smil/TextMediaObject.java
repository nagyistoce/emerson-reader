package org.daisy.reader.model.smil;

public class TextMediaObject extends MediaObject {
	/**
	 * Constructor.
	 * @param id The value of the XML audio@id
	 * @param src The value of the XML audio@src
	 * @param parent The time container in which this audio object appears
	 */
	public TextMediaObject(String id, String src, TimeContainer parent) {
		super(id,src,parent);
	}	
	
}