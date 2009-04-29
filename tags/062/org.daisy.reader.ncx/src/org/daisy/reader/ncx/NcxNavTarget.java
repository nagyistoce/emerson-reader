package org.daisy.reader.ncx;

import org.daisy.reader.model.semantic.Semantic;

/**
 * A singular entry in an NCX NavList.
 * @author Markus Gylling
 */
public class NcxNavTarget extends NcxItem  {
	NcxNavList parent;
		
	public NcxNavTarget(NcxNavList parent, String label, String target, int playOrder) {
		super(label,target,playOrder);
		this.parent = parent;			
	}
		
	public NcxNavList getParentList() {		
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigationItem#getSemantic()
	 */
	public Semantic getSemantic() {		
		return parent.getSemantic();
	}
}
