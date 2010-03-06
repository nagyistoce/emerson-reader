package org.daisy.reader.ncx;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.daisy.reader.model.navigation.Direction;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.navigation.INavigationItem;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.ModelPositionChangeEvent;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.position.URIPosition;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.smil.AudioMediaObject;

/**
 * Represent the Daisy Z3986 NCX as an implementation of INavigation. 
 * @author Markus Gylling
 */

/*
 * Ncx implements IPositionChangeListener (via INavigation)
 * but doesnt subscribe to events. The Model notifies its
 * INavigation separately before broadcasting the event
 * to make sure the entire model is updated prior to broadcast.
 */
public class Ncx implements INavigation {
	NcxNavMap navMap = null;
	List<NcxNavList> navLists = null;
	Map<String,NcxCustomTest> ncxCustomTestData = null; //ID-in-NCX, CustomTest
	
	int currentPlayOrder = 1;
	protected URL ncxURL;
	
	public Ncx() {		
		navLists = new ArrayList<NcxNavList>();
		ncxCustomTestData = new HashMap<String,NcxCustomTest>();
		navMap = new NcxNavMap(this);
	}
			
	public void load(URL ncx) {
		this.ncxURL = ncx;				
		try {
			NcxLoader.load(this);
		} catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
		}
		//System.err.println(this.navMap.toString());
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.reader.model.position.IPositionChangeListener#positionChanged(org.daisy.reader.model.position.ModelPositionChangeEvent)
	 */
	public void positionChanged(ModelPositionChangeEvent event) {
		
		//find out if any of the targets in the ncx reference this position
		//if so, update currentPlayOrder
		NcxItem ref = getNavigationItem(event.getNewPosition());
		
		if(ref!=null) {
			//System.err.println("Ncx#positionChanged: new current item: " + ref.toString()); //$NON-NLS-1$
			currentPlayOrder = ref.playOrder;
		}
		
	}
	

	/**
	 * Get a NcxItem that targets given IPosition, or null
	 * if no referencing NcxItem is found.
	 */
	private NcxItem getNavigationItem(IPosition position) {
		
		NcxItem item = null;
		
		try{
			if(position instanceof SmilAudioPosition) {
				//TODO we could do some optimizations here, 
				//for example assuming that the new position is 
				//adjacent-next to current position				
				//And we could get an ISelection from NavigatorView for
				//the case where the user activates a heading
															
				NcxNavList pageList = getNavList(Semantic.PAGE_NUMBER);
				if(pageList!=null) {
					item = getNcxItem(pageList.iterator(),
							((SmilAudioPosition)position).getSmilAudioMediaObject());
				}				
				if(item==null) {
					item = getNcxItem(navMap.listIterator(),
							((SmilAudioPosition)position).getSmilAudioMediaObject());
				}
			}else if(position instanceof URIPosition) {
				URIPosition uriPosition = (URIPosition) position;
				NcxNavList pageList = getNavList(Semantic.PAGE_NUMBER);
				URI absolute = uriPosition.getAbsoluteURI();
				if(pageList!=null) {
					item = getNcxItem(pageList.iterator(),absolute);
				}
				
				if(item==null) {
					item = getNcxItem(navMap.listIterator(),absolute);
				}
			}
		}catch (Exception e) {
			Activator.getDefault().logError(e.getLocalizedMessage(), e);
			return null;
		}
		return item;
			
	}

	/**
	 * Get an NcxItem whose target URI refers to given AudioMediaObject 
	 * (or relevant peers, depending on the nature of the SMIL presentation).
	 * @param iterator An iterator of NcxItems
	 * @param phrase The sought target.
	 * @return An NcxItem, or null if no NcxItem refers to given AudioMediaObject.
	 */
	protected NcxItem getNcxItem(Iterator<?> iterator, AudioMediaObject phrase) {
		NcxItem navItem = null;
		/*
		 * This gets a little messy since we do not know off the bat to where in the SMIL tree an
		 * NCX URI points, we need to look at the phrases parents as well 		
		 */
		final String targetFileName = phrase.getParentContainer().getParentFile().getLocalName();
		final String targetAudioID = phrase.getID();
		final String targetParentTimeContainerID = phrase.getParentContainer().getID();
		String targetParentParentTimeContainerID = null;
		if(phrase.getParentContainer().getParentContainer()!=null)
			targetParentParentTimeContainerID = phrase.getParentContainer().getParentContainer().getID();
				
		while(iterator.hasNext()) {
			navItem = (NcxItem) iterator.next();
			//TODO should use relative URI instead of filelocalname
			if(targetFileName.equals(navItem.getTargetFileLocalName())) {
				String targetID = navItem.getTargetFragment();
				if(targetAudioID!=null 
						&& targetAudioID.equals(targetID)) return navItem;
				if(targetParentTimeContainerID!=null 
						&& targetParentTimeContainerID.equals(targetID)) return navItem;
				if(targetParentParentTimeContainerID!=null 
						&& targetParentParentTimeContainerID.equals(targetID)) return navItem;			 					
			} 
		}
		
		return null;
	}
	
	/**
	 * Get an NcxItem whose target URI refers to given URI.
	 * @param iterator An iterator of NcxItems
	 * @param URI target The sought target.
	 * @return An NcxItem, or null if no NcxItem refers to given AudioMediaObject.
	 */
	private NcxItem getNcxItem(Iterator<?> iterator, URI target) {
		NcxItem navItem = null;
		try {
			URI ncxURI = this.ncxURL.toURI();
			while(iterator.hasNext()) {
				navItem = (NcxItem) iterator.next();				
				URI navItemURI = ncxURI.resolve(navItem.target);
				if(navItemURI.compareTo(target)==0) return navItem;
			}	
		} catch (URISyntaxException e) {				
			e.printStackTrace();
		}
		return null;
	}	
		
	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getAdjacentPosition(org.daisy.raymond.model.navigation.Direction, org.daisy.raymond.model.semantic.Semantic)
	 */
	public IPosition getAdjacentPosition(Direction direction, Semantic semantic) {
		//TODO this could be speeded up if we had hashmaps where key = playorder
		
		NcxItem destination = null;
		Iterator<?> iterator = null;
		
		if(semantic == Semantic.PAGE_NUMBER) {
			NcxNavList pageList = getNavList(semantic);
			if(pageList==null) return null;
			iterator = pageList.iterator();			
		}else if(semantic == Semantic.HEADING) {
			iterator = navMap.listIterator();
		}else{
			return null;
		}
		
		if(direction == Direction.NEXT) {
			while(iterator.hasNext()) {
				NcxItem item = (NcxItem) iterator.next();
				if(item.playOrder > currentPlayOrder) {
					destination = item;
					break;
				}
			}
		}else {
			while(iterator.hasNext()) {
				NcxItem item = (NcxItem) iterator.next();
				if(item.playOrder < currentPlayOrder) {
					destination = item;					
				}else{
					break;
				}
			}
		}
		
//		String value = (destination!=null)?destination.toString():"null";
//		System.err.println("return value of Ncx.getAdjacentPosition is " 
//				+ value);
		
		return (destination!=null) 
			? new URIPosition(destination.target,ncxURL) 
			: null;
				
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getCurrent(org.daisy.raymond.model.semantic.Semantic)
	 */
	public INavigationItem getCurrent(Semantic semantic) {
		//for given semantic, return the item that matches exactly
		//or is minimally lower than currentPlayOrder
		
		//TODO this could be speeded up if we had hashmaps where key = playorder
		//and if currentPlayorder was an NcxItem instead of NcxItem playorder
		
		NcxItem current = null;
		
		Iterator<?> iterator = null;
		
		if(semantic == Semantic.PAGE_NUMBER) {
			NcxNavList pageList = getNavList(semantic);
			if(pageList==null) return null;
			iterator = pageList.iterator();
		}else if(semantic == Semantic.HEADING) {
			iterator = navMap.listIterator();
		}
		
		while(iterator.hasNext()) {
			NcxItem ncxItem = (NcxItem) iterator.next();
			//loop until we have a match or the closest preceeding
			if(ncxItem.playOrder <= currentPlayOrder) {
				current = ncxItem;
			}else{
				break;
			}
		}
				
		return (current!=null) 
			? (INavigationItem)current 
			: null;
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getFirst(org.daisy.raymond.model.semantic.Semantic)
	 */
	public INavigationItem getFirst(Semantic semantic) {
		try{
			if(semantic == Semantic.HEADING) {
				return navMap.children.get(0);
			}
			else if(semantic == Semantic.PAGE_NUMBER) {
				NcxNavList pageList = getNavList(Semantic.PAGE_NUMBER);
				if(pageList!=null) {
					return pageList.get(0);
				}
			}
		}catch (Exception e) {
		
		}	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getLast(org.daisy.raymond.model.semantic.Semantic)
	 */
	public INavigationItem getLast(Semantic semantic) {
		try{
			if(semantic == Semantic.HEADING) {
				//get the last toplevel point
				NcxNavPoint start = navMap.children.get(navMap.children.size()-1);
				//find its last leaf
				return getUltimateLeaf(start);				
			}
			else if(semantic == Semantic.PAGE_NUMBER) {
				NcxNavList pageList = getNavList(Semantic.PAGE_NUMBER);
				if(pageList!=null) {
					return pageList.get(pageList.size()-1);
				}
			}
		}catch (Exception e) {
		
		}	
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getParent(org.daisy.raymond.model.navigation.INavigationItem)
	 */
	public INavigationItem getParent(INavigationItem item) {
		if(item==null || !(item instanceof NcxNavPoint))  return null;		
		NcxNavPoint navPoint = (NcxNavPoint) item;		
		Object parent = navPoint.parent;
		if(parent instanceof NcxNavMap) return null; //toplevel
		return (NcxNavPoint) parent;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.raymond.model.navigation.INavigation#getSourceURL()
	 */
	public URL getSourceURL() {		
		return ncxURL;
	}

	
	@SuppressWarnings("unchecked")
	public ListIterator<INavigationItem> listIterator(Semantic semantic) {
		if(semantic == Semantic.PAGE_NUMBER) {
			List<?> pageList = getNavList(semantic);
			if(pageList!=null) {
				return ((List<INavigationItem>)pageList).listIterator();
			}	
		}else if (semantic == Semantic.HEADING) {
			return navMap.listIterator();
		}
		return null;
	}

	/**
	 * Retrieve a certain SMIL customTest by looking
	 * att the bookstruct mapping in NCX head.
	 * <p>The weird fact that this data is available from this class is inherited from the spec.</p>
	 * @return a CustomTest, or null if no CustomTest was mapped to the given ID value.
	 */
	public NcxCustomTest getCustomTest(String customTestID) {
		return ncxCustomTestData.get(customTestID);			
	}

	/**
	 * Get a NcxNavList matching the given semantic, or
	 * null if no match was found.
	 */
	public NcxNavList getNavList(Semantic semantic) {
		for(NcxNavList list : navLists) {
			if(list.getSemantic() == semantic) return list; 
		}
		return null;
	}
	
	/**
	 * Add a navlist of a certain type, if it doesnt already exist
	 * @param semantic The type of NavList to add
	 * @return The NavList of the given semantic
	 */
	public NcxNavList addNavList(Semantic semantic) {
		if(getNavList(semantic) == null) {
			navLists.add(new NcxNavList(semantic));
		}
		return getNavList(semantic);
	}

	private INavigationItem getUltimateLeaf(NcxNavPoint navPoint) {
		if(navPoint.children.isEmpty()) return navPoint;
		return getUltimateLeaf(navPoint.children.get(navPoint.children.size()-1));		
	}

	public NcxNavMap getNavMap() {
		return this.navMap;		
	}

	@Override
	public String toString() {
		return this.navMap.toString();
	}	

}
