package org.daisy.reader.ncx;

/**
 * A singular entry in an NCX pageList.
 * @author Markus Gylling
 */

public class NcxPageTarget extends NcxNavTarget {

//	private int value;
//	private String type;

//	public NcxPageTarget(NcxNavList parent, String label, String target, int playOrder, int value, String type) {
//		super(parent, label, target, playOrder);
//		this.value = value;
//		this.type = type;		
//	}

	public NcxPageTarget(NcxNavList parent, String label, String target, int playOrder) {
		super(parent, label, target, playOrder);
	}
	
//	public int getValue() {
//		return value;
//	}

//	public String getType() {
//		return type;
//	}
	
	

}
