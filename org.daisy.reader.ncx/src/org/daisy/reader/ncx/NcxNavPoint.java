package org.daisy.reader.ncx;

import java.util.ArrayList;
import java.util.List;

import org.daisy.reader.model.semantic.Semantic;

/**
 * A singular entry in the NCX NavMap.
 * @author Markus Gylling
 */
public class NcxNavPoint extends NcxItem {
	Object parent;
	List<NcxNavPoint> children = null;
		
	public NcxNavPoint(Object parent, String label, String target, int playOrder) {
		super(label,target,playOrder);
		this.parent = parent;
		children = new ArrayList<NcxNavPoint>();
	}
		
	/**
	 * Retrieve the parent of this navPoint, which is
	 * the NcxNavMap in the case of top level NavPoint,
	 * else another real NavPoint.
	 */
	public Object getParent() {
		return parent;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigationItem#getSemantic()
	 */
	public Semantic getSemantic() {		
		return Semantic.HEADING;
	}

	/**
	 * Get the next sibling of this navPoint, or null
	 * if no next sibling exists
	 */
	public NcxNavPoint getNextSibling() {		
		List<NcxNavPoint> parentChildren;
		
		if(parent instanceof NcxNavMap) {
			parentChildren = ((NcxNavMap)parent).children;
		}else{
			parentChildren = ((NcxNavPoint)parent).children;
		}		
		int curIndex = parentChildren.indexOf(this);
		
		return (curIndex<parentChildren.size()-1) 
			? parentChildren.get(++curIndex) : null;
				
	}
	
	/**
	 * Get the previous sibling of this navPoint, or null
	 * if no previous sibling exists
	 */
	public NcxNavPoint getPreviousSibling() {		
		List<NcxNavPoint> parentChildren;
		
		if(parent instanceof NcxNavMap) {
			parentChildren = ((NcxNavMap)parent).children;
		}else{
			parentChildren = ((NcxNavPoint)parent).children;
		}		
		int curIndex = parentChildren.indexOf(this);
		
		return (curIndex>0) ? parentChildren.get(curIndex--) : null;
				
	}

	public List<NcxNavPoint> getChildren() {
		return children;
	}
	
	

}
