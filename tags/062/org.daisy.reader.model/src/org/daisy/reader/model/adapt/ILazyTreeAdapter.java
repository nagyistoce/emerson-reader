package org.daisy.reader.model.adapt;

public interface ILazyTreeAdapter {
	
    /**
     * Returns the parent for the given element, or <code>null</code> 
     * indicating that the parent can't be computed. 
     * In this case the tree-structured viewer can't expand
     * a given node correctly if requested.
     *
     * @param element the element
     * @return the parent element, or <code>null</code> if it
     *   has none or if the parent cannot be computed
     */
	public Object getParent(Object element);

	public int getChildCount(Object element);
	
	public Object getChild(Object parent, int index);
	
}
