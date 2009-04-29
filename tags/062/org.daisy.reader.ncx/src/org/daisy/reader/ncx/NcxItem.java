package org.daisy.reader.ncx;

import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.util.StringUtils;
import org.daisy.reader.util.URIStringParser;

public abstract class NcxItem implements INavigationItem {
	String label;
	String target;
	int playOrder;
	private boolean isLabelWhitespaceNormalized = false;
	
	/** Substring of target */
	private String targetLocalName;
	
	/** Substring of target */
	private String targetFragment;
	
	public NcxItem(String label, String target, int playOrder) {
		this.label = label;
		this.target = target;
		this.playOrder = playOrder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigationItem#getLabel()
	 */
	public String getLabel() {		
		if(!isLabelWhitespaceNormalized) {
			label = StringUtils.normalizeWhitespace(label);
			isLabelWhitespaceNormalized = true;
		}	
		return label;
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigationItem#getTarget()
	 */
	public String getTarget() {
		return target;
	}
	
	public String getTargetFileLocalName() {
		if(targetLocalName==null)
			targetLocalName = URIStringParser.getFileLocalName(target);
		return targetLocalName;
	}
	
	/**
	 * Retrieve the target fragment.
	 */
	public String getTargetFragment() {
		if(targetFragment==null) {
			targetFragment = URIStringParser.getFragment(target);
		}	
		return targetFragment;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append(' ');
		sb.append("target: ").append(target).append(' '); //$NON-NLS-1$
		sb.append("label: ").append(getLabel()).append(' '); //$NON-NLS-1$
		sb.append("playOrder: ").append(playOrder).append(' '); //$NON-NLS-1$
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.navigation.INavigationItem#getSequence()
	 */
	public int getOrdinal() {		
		return playOrder;
	}
}
