package org.daisy.reader.ncx;

import org.daisy.reader.model.adapt.ILabelAdapter;
import org.daisy.reader.model.adapt.ILazyTreeAdapter;
import org.eclipse.core.runtime.IAdapterFactory;

public class NcxAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType==ILabelAdapter.class) {
			if(adaptableObject instanceof NcxItem) {
				return new NcxItemLabelAdapter();
			}
		}else if(adapterType==ILazyTreeAdapter.class) {
			if(adaptableObject instanceof NcxNavPoint) {
				return new NcxLazyTreeNavPointAdapter();
			}else if(adaptableObject instanceof Ncx) {
				return new NcxLazyTreeAdapter();
			}
		} 
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {		
		return new Class[]{ILabelAdapter.class, ILazyTreeAdapter.class};
	}
	
	class NcxLazyTreeAdapter implements ILazyTreeAdapter {

		public Object getChild(Object parent, int index) {			
			return ((Ncx)parent).navMap.children.get(index);
		}

		public int getChildCount(Object element) {
			return ((Ncx)element).navMap.children.size();
		}

		public Object getParent(Object element) {	
			System.err.println("CHECK THIS NCXADAPTERFACTORY"); //$NON-NLS-1$
			return ((Ncx)element).navMap;
		}
		
	}
	
	class NcxLazyTreeNavPointAdapter implements ILazyTreeAdapter {

		public NcxNavPoint getChild(Object parent, int index) {
			return ((NcxNavPoint)parent).children.get(index);			
		}

		public int getChildCount(Object element) {
			return ((NcxNavPoint)element).children.size();
		}

		public Object getParent(Object element) {
			//romain 20100223: return the Ncx instead of
			//the navMap in order to fix the no-highlight
			//bug in the ncx treeview
			Object obj = ((NcxNavPoint)element).parent;
			if(obj instanceof NcxNavMap) {
				return ((NcxNavMap)obj).getNcx();
			}else {
				return obj;
			}
		}

	}
	
	class NcxItemLabelAdapter implements ILabelAdapter {

		public String getLabel(Object o) {			
			return ((NcxItem)o).getLabel();
		}
		
	}

}
