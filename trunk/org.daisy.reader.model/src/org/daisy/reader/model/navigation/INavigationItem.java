package org.daisy.reader.model.navigation;

import org.daisy.reader.model.semantic.Semantic;

/**
 * An atomic member of an INavigation instance
 * @author Markus Gylling
 */
public interface INavigationItem {

	/**
	 * Retrieve a String representation of the target
	 * this INavigationItem points to. This is typically,
	 * but does not have to be, a relative URI.
	 * <p>This method never returns null.</p>
	 */
	public String getTarget();
	
	/**
	 * Retrieve the semantic of this INavigationItem. 
	 * <p>This method never returns null.</p>
	 */
	public Semantic getSemantic();
	
	/**
	 * Retrieve a textual label of this INavigationItem.
	 * <p>This typically describes a property of the 
	 * target resource.</p>
	 * <p>This method never returns null.</p>
	 * @see #getTarget()
	 */
	public String getLabel();
	
	/**
	 * Retrieve a sequential identifier of this INavigationItem, 
	 * relative to the other INavigationItems of the parent INavigation.
	 */
	public int getOrdinal();
	
}
