package org.daisy.reader.model.navigation;

import java.net.URL;
import java.util.ListIterator;

import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.IPositionChangeListener;
import org.daisy.reader.model.semantic.Semantic;

public interface INavigation extends IPositionChangeListener {
	
	/**
	 * Retrieve an IPosition with the given semantic in the given direction, in 
	 * relation to the position reflected by the current positional state
	 * of this INavigation instance. 
	 * @return an IPosition reflecting an adjacent position, or null of no such
	 * position could be retrieved
	 * @see #getCurrent(Semantic)
	 */	
	public IPosition getAdjacentPosition(Direction direction, Semantic semantic);
	
	/**
	 * Retrieve an INavigationItem from this INavigations data model that represents its
	 * current positional state.
	 * @param semantic The semantic of the sought object
	 * @return An INavigationItem, or null if no current positional state for the given
	 * semantic exists.
	 */
	public INavigationItem getCurrent(Semantic semantic);
	
	/**
	 * Retrieve an INavigationItem from this INavigations data model that represents the
	 * first occurence in temporal order of the given semantic.
	 * @param semantic The semantic of the sought object
	 * @return An INavigationItem, or null if no object with the given
	 * semantic exists.
	 */
	public INavigationItem getFirst(Semantic semantic);
	
	/**
	 * Retrieve an INavigationItem from this INavigations data model that represents the
	 * last occurence in temporal order of the given semantic.
	 * @param semantic The semantic of the sought object
	 * @return An INavigationItem, or null if no object with the given
	 * semantic exists.
	 */
	public INavigationItem getLast(Semantic semantic);
	
	/**
	 * Retrieve the parent of the given item in a tree view of the data. 
	 * @param item The item to retrieve the parent of.
	 * @return A parent INavigationItem if a parent existed, else null. Null
	 * is returned if the given item is not a heading, or if the given
	 * item is a level one heading.
	 */
	public INavigationItem getParent(INavigationItem item);

	/**
	 * Retrieve a ListIterator over the INavigationItems with the given semantic
	 * in presentation order.
	 * <p>Atomic items retrieved from this iterator can be modified, but reordering
	 * will not be reflected in the backing model.</p>
	 * @return a ListIterator, that may be empty but never null.
	 */
	public ListIterator<INavigationItem> listIterator(Semantic semantic);

	/**
	 * Retrieve the URL of the resource that was the source for creating this object.
	 */
	public URL getSourceURL();
	
}
