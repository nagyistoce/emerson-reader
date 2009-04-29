package org.daisy.reader.ncx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.daisy.reader.model.navigation.INavigationItem;

public class NcxNavMap  {
	List<NcxNavPoint> children = null;
	
	public NcxNavMap() {
		children = new ArrayList<NcxNavPoint>();
	}
	
	/**
	 * Get the top-level children (level one headings) of this NavMap.
	 */
	public List<NcxNavPoint> getChildren() {
		return children;
	}

	public ListIterator<INavigationItem> listIterator() {		
		return new NavMapListIterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<INavigationItem> iter = this.listIterator();
		while(iter.hasNext()) {
			NcxNavPoint np = (NcxNavPoint)iter.next();
			sb.append(np.toString()).append("\n"); //$NON-NLS-1$
		}	
		return sb.toString();
	}	
	
	class NavMapListIterator implements ListIterator<INavigationItem> {
		NcxNavPoint curNavPoint;
		int curIndex;
		
		public NavMapListIterator() {			
			curIndex = 0;
		}
		
	    /**
	     * Returns <tt>true</tt> if this list iterator has more elements when
	     * traversing the list in the forward direction. (In other words, returns
	     * <tt>true</tt> if <tt>next</tt> would return an element rather than
	     * throwing an exception.)
	     *
	     * @return <tt>true</tt> if the list iterator has more elements when
	     *		traversing the list in the forward direction.
	     */
		public boolean hasNext() {
			if(curNavPoint==null) {
				//start of iteration, does the navMap have any children?
				return !children.isEmpty();
			} 
			//else not start of iteration
			
			//if current has children
			if(!curNavPoint.children.isEmpty()) return true;
			//if current has nextSibling
			if(curNavPoint.getNextSibling()!=null) return true;
			//if currents parent hierarchy has a next sibling
			return getParentHierarchyNextSibling(curNavPoint)!=null;			
		}

	    private NcxNavPoint getParentHierarchyNextSibling(NcxNavPoint navPoint) {
			Object o = navPoint.parent;
			if(o instanceof NcxNavMap) return null;
			NcxNavPoint parent = (NcxNavPoint) o;
			NcxNavPoint next = parent.getNextSibling(); 
			if(next!=null) return next;
			return getParentHierarchyNextSibling(parent);
		}

		/**
	     * Returns <tt>true</tt> if this list iterator has more elements when
	     * traversing the list in the reverse direction.  (In other words, returns
	     * <tt>true</tt> if <tt>previous</tt> would return an element rather than
	     * throwing an exception.)
	     *
	     * @return <tt>true</tt> if the list iterator has more elements when
	     *	       traversing the list in the reverse direction.
	     */
		public boolean hasPrevious() {
			if(curNavPoint==null) {
				//start of iteration
				return false;
			}
			//if has a previous sibling
			if(curNavPoint.getPreviousSibling()!=null) return true;
			//if is not a toplevel heading (if it is, parent is NavMap)
			return (curNavPoint.parent instanceof NcxNavPoint);
		}

	    /**
	     * Returns the next element in the list.  This method may be called
	     * repeatedly to iterate through the list, or intermixed with calls to
	     * <tt>previous</tt> to go back and forth.  (Note that alternating calls
	     * to <tt>next</tt> and <tt>previous</tt> will return the same element
	     * repeatedly.)
	     *
	     * @return the next element in the list.
	     * @exception NoSuchElementException if the iteration has no next element.
	     */
		public INavigationItem next() {
			
			if(curNavPoint==null) {
				//start of iteration
				curNavPoint = children.get(0);
				return curNavPoint;
			} else { 
				//not start of iteration		
				NcxNavPoint newPos = null;
		
				//first, return the first child if exists
				if(!curNavPoint.children.isEmpty()) {
					newPos = curNavPoint.children.get(0);
				}
				
				//if no child, try next sibling
				if(newPos == null) {
					newPos = curNavPoint.getNextSibling();				
				}
				
				//if no next sibling, try parent hierarchy next sibling
				if(newPos == null) {
					newPos = getParentHierarchyNextSibling(curNavPoint);			
				}
				
				if(newPos != null) {
					curNavPoint = newPos;
					curIndex++;
					return curNavPoint;
				}
			}
			throw new NoSuchElementException();
			
		}

	    /**
	     * Returns the previous element in the list.  This method may be called
	     * repeatedly to iterate through the list backwards, or intermixed with
	     * calls to <tt>next</tt> to go back and forth.  (Note that alternating
	     * calls to <tt>next</tt> and <tt>previous</tt> will return the same
	     * element repeatedly.)
	     *
	     * @return the previous element in the list.
	     *
	     * @exception NoSuchElementException if the iteration has no previous
	     *            element.
	     */
		public INavigationItem previous() {
			
			if(curNavPoint!=null) {
				Object newPos = null;
				newPos = curNavPoint.getPreviousSibling();				
			
				if(newPos==null) {
					newPos = curNavPoint.parent;
				}
				
				if(newPos != null && newPos instanceof NcxNavPoint) {
					curNavPoint = (NcxNavPoint)newPos;
					curIndex--;
					return curNavPoint;
				}
			}
			throw new NoSuchElementException();
		}
		
	    /**
	     * Returns the index of the element that would be returned by a subsequent
	     * call to <tt>next</tt>. (Returns list size if the list iterator is at the
	     * end of the list.)
	     *
	     * @return the index of the element that would be returned by a subsequent
	     * 	       call to <tt>next</tt>, or list size if list iterator is at end
	     *	       of list.
	     */
		public int nextIndex() {
			if(!hasNext()) return curIndex;
			return curIndex+1;			
		}

	    /**
	     * Returns the index of the element that would be returned by a subsequent
	     * call to <tt>previous</tt>. (Returns -1 if the list iterator is at the
	     * beginning of the list.)
	     *
	     * @return the index of the element that would be returned by a subsequent
	     * 	       call to <tt>previous</tt>, or -1 if list iterator is at
	     *	       beginning of list.
	     */
		public int previousIndex() {
			if(curIndex==0) return -1;
			return curIndex-1;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#remove()
		 */
		public void remove() {
			throw new IllegalStateException();			
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(INavigationItem e) {
			throw new IllegalStateException();
			
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(INavigationItem e) {
			throw new IllegalStateException();			
		}
		
	}
}	


