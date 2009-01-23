package org.daisy.reader.model.smil;

import java.net.URL;

public interface ISmilLoader {

	/**
	 * Load a SmilFile, populating its fields.
	 * @param source URL of a SMIL file.
	 * @param smilFile The instance to populate.
	 * @return true if load was successful, else false
	 */
	public boolean load(URL source, SmilFile smilFile);
	
}
