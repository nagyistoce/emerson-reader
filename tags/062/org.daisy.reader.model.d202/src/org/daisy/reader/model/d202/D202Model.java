package org.daisy.reader.model.d202;

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
 * A model for DAISY 2.02 DTBs
 * @author Markus Gylling
 */
public class D202Model extends DtbModel {
	
	public D202Model(URL url, INavigation navigation, Metadata metadata, SmilSpine spine) {
		super(url, navigation, metadata,spine);
	}
		
	@Override
	public Object getProperty(String key) throws PropertyException {
		if(IPropertyConstants.PUBLICATION_TYPE.equals(key)) {
			return new PublicationType(Messages.D202Model_daisy, "DAISY 2.02",this.getClass()); //$NON-NLS-1$
		} else if(key.equals(IDtbTextContentSubstitutor.KEY)) {
			return null; //no content substitution in D202
		}
		return super.getProperty(key);
	}
	
	@Override
	public void setProperty(String key, Object value) throws PropertyException {
		throw new PropertyException(key);		
	}
	
}