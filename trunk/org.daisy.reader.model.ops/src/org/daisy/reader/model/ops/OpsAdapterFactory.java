package org.daisy.reader.model.ops;

import java.net.URL;

import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.adapt.ITextURLAdapter;
import org.daisy.reader.model.position.URIPosition;
import org.eclipse.core.runtime.IAdapterFactory;

public class OpsAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ITextURLAdapter.class) {
			if(adaptableObject instanceof PackageFileItem ) {
				return new PackageFileItemAdapter();
			}	
			if(adaptableObject instanceof URIPosition) {
				if(ModelManager.getModel()!=null 
						&& ModelManager.getModel() instanceof OpsModel)					
				return new URIPositionAdapter();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {	
		return new Class[]{PackageFileItemAdapter.class,URIPositionAdapter.class};
	}
		
	class PackageFileItemAdapter implements ITextURLAdapter {
		public URL getURL(Object o) {
			return ((PackageFileItem)o).mItemURL;			
		}
	}
	
	class URIPositionAdapter implements ITextURLAdapter {

		public URL getURL(Object o) {
			try {
				return ((URIPosition)o).getAbsoluteURI().toURL();
			} catch (Exception e) {
				Activator.getDefault().logError(e.getLocalizedMessage(), e);
			}
			return null;
		}
		
	}

}
