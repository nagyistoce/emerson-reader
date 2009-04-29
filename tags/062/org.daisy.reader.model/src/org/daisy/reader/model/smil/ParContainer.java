package org.daisy.reader.model.smil;

public class ParContainer extends TimeContainer {

	/**
	 * A SMIL par time container.
	 * @param id The XML id of this container, may be null.
	 * @param parentContainer The parent container of this container.
	 * @param file. The SmilFile parent of this container. Is never null.
	 */
	public ParContainer(String id, TimeContainer parentContainer, SmilFile file) {
		super(id, parentContainer, file);
	}
	
}
