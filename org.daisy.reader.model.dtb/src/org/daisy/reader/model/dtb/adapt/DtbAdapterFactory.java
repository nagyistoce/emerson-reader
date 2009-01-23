package org.daisy.reader.model.dtb.adapt;

import java.net.URL;
import java.util.List;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.adapt.ITextURLAdapter;
import org.daisy.reader.model.dtb.DtbModel;
import org.daisy.reader.model.dtb.IDtbTextContentSubstitutor;
import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.smil.MediaObject;
import org.daisy.reader.model.smil.SmilFile;
import org.daisy.reader.model.smil.SmilUtils;
import org.eclipse.core.runtime.IAdapterFactory;

public class DtbAdapterFactory implements IAdapterFactory {
	DtbTextAdapter textAdapter;
	
	public DtbAdapterFactory() {
		
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == ITextURLAdapter.class) {
			Model model = ModelManager.getModel();
			if(model!=null && !model.isDisposed() && model instanceof DtbModel) {
				if(adaptableObject instanceof SmilAudioPosition) {								
					return getDtbTextAdapter();										
				}
				if(adaptableObject instanceof SmilFile) {
					return new SmilFileURLAdapter();
				}
			}
			
		}

		return null;
	}
	
	class SmilFileURLAdapter implements ITextURLAdapter {

		public URL getURL(Object o) {
			SmilFile s = (SmilFile)o;
			return s.getURL();
		}
		
	}
	
	class DtbTextAdapter implements ITextURLAdapter {
												
		public URL getURL(Object o) {
			
			SmilAudioPosition sap = (SmilAudioPosition)o;
						
			//get the relevant SMIL text object
			List<MediaObject> texts = SmilUtils.getTextObjects(sap);
			if(texts==null || texts.isEmpty()) return null;			
			MediaObject text = texts.get(texts.size()-1);
			
			//query the model for a substitute file
			DtbModel model = (DtbModel)ModelManager.getModel();	
			URL substitute = null;			
			try {
				IDtbTextContentSubstitutor substitutor = (IDtbTextContentSubstitutor) 
						model.getProperty(IDtbTextContentSubstitutor.KEY);
				
				if(substitutor!=null) 
					substitute = substitutor.substitute(text.getURL());
				
			} catch (PropertyException e) { }						
												
			return (substitute!=null)?substitute:text.getURL(); 						

		}
	}
	
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[]{DtbTextAdapter.class, SmilFileURLAdapter.class};		
	}

	private DtbTextAdapter getDtbTextAdapter() {
		if(textAdapter==null) {
			textAdapter = new DtbTextAdapter();
		}
		return textAdapter;
	}
}
