package org.daisy.reader.model.z2005;

import java.net.URL;

import org.daisy.reader.model.dtb.DtbModel;
import org.daisy.reader.model.dtb.IDtbTextContentSubstitutor;
import org.daisy.reader.model.exception.PropertyException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.model.smil.SmilSpine;

/**
 * A model for Z39.96-2005 DTBs
 * @author Markus Gylling
 */
public class Z2005Model extends DtbModel {
	//access through getProperty instead of AdapterFactory since they are
	//model specific, and RCP AdapterFactory will just return the first
	//match, even if you return null on getAdapter... ?
	Z2005DtbookSubstitutor substitutor; 
	
	public Z2005Model(URL url, INavigation navigation, Metadata metadata, SmilSpine spine) {
		super(url, navigation, metadata,spine);		
		//TODO make substituion a config ini option
		substitutor = new Z2005DtbookSubstitutor(this);
	}

	@Override
	public Object getProperty(String key) throws PropertyException {
		if(key.equals(IDtbTextContentSubstitutor.KEY)) {
			return substitutor;			
		}else if(IPropertyConstants.PUBLICATION_TYPE.equals(key)) {
			return new PublicationType(Messages.Z2005Model_daisy, "ANSI/NISO Z39.86-2005",this.getClass()); //$NON-NLS-1$
		}
		return super.getProperty(key);
	}
	
	@Override
	public void setProperty(String key, Object value) throws PropertyException {
		throw new PropertyException(key);
	}
		
	@Override
	protected void doDispose() {
		if(substitutor!=null)
			substitutor.dispose();
		super.doDispose();
	}
	
	

}
