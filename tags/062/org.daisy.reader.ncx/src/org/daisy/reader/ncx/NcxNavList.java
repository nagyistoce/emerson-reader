package org.daisy.reader.ncx;

import java.util.ArrayList;

import org.daisy.reader.model.semantic.Semantic;

public class NcxNavList extends ArrayList<NcxNavTarget> {

	private Semantic semantic;

	public NcxNavList(Semantic semantic) {
		this.semantic = semantic;
	}
	
	public Semantic getSemantic() {
		return this.semantic;
	}
	
	private static final long serialVersionUID = 2253741631146098785L;

}
