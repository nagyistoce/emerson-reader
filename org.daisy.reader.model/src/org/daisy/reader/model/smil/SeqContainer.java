package org.daisy.reader.model.smil;

public class SeqContainer extends TimeContainer {

	/**
	 * A SMIL seq time container.
	 * @param id The XML id of this container, may be null.
	 * @param parentContainer The parent container of this container, may be null if this is a root container
	 * @param file. The SmilFile parent of this container. Is never null.
	 */
	public SeqContainer(String id, TimeContainer parentContainer, SmilFile file) {
		super(id, parentContainer, file);
	}

}
