package org.daisy.reader.model.metadata;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.daisy.reader.util.StAXFactoryProxy;
import org.daisy.reader.util.StringUtils;

/**
 * Provide publication-level bibliographic and identity metadata.
 * <p>Clients may subclass this class.</p>
 * @author Markus Gylling
 */
public class Metadata extends HashMap<String,String> {
	private URL source;
	
	/**
	 * Constructor
	 * @param xmlsource An XML file from which the metadata was read. This
	 * is used to create a UID if none is explicitly set.
	 */
	public Metadata(URL xmlsource) {
		this.source = xmlsource;
	}
		
	@Override
	public String get(Object key) {
		if(key.equals(Metadata.UUID) && super.get(key)==null) {
			//generate a UUID that will be the same for each calc
			super.put(Metadata.UUID, getUID(source));
		}
		return super.get(key);
	}
	
	private String getUID(URL source) {
		XMLInputFactory xif = StAXFactoryProxy.getXMLInputFactory();
		InputStream is = null;
		XMLStreamReader xsr = null;
		StringBuilder sb = new StringBuilder();
		try{
			is = source.openStream();
			xsr = xif.createXMLStreamReader(is);
			while(xsr.hasNext()) {
				int type = xsr.next();				
				if(type == XMLEvent.CHARACTERS){
					sb.append(trim(xsr.getText()));
				}
				if(sb.length()>256) break; 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {			
			try {
				if(is!=null)is.close();
				if(xsr!=null)xsr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String result = StringUtils.toRestrictedSubset(
				StringUtils.FilenameRestriction.Z3986, sb.toString());
		
		if(result.length()>64) result = result.substring(result.length()-64, result.length());
		
		//give up creating a repeatable one
		if(result.length()<1) {
			result = java.util.UUID.randomUUID().toString();
			if(result.length()>48)
			result = result.substring(0, 48);
		}	
		
		return result;
		
	}

	private String trim(String data) {
		StringBuilder sb = new StringBuilder();		
		int codePointCount = data.codePointCount(0, data.length());
		for (int i = 0; i < codePointCount; i++) {
			int codePoint = data.codePointAt(i);
			if(Character.isLetter(codePoint)) {
				sb.append(Character.toChars(codePoint));
			}
		}
		return sb.toString();
	}

	public final static String UUID = "METADATA_KEY_UNIQUE_IDENTIFIER"; //$NON-NLS-1$
	public final static String TITLE = "METADATA_KEY_TITLE"; //$NON-NLS-1$
	public final static String AUTHOR = "METADATA_KEY_AUTHOR"; //$NON-NLS-1$
	public final static String PUBLISHER = "METADATA_KEY_PUBLISHER"; //$NON-NLS-1$
	
	private static final long serialVersionUID = 374106690550597657L;
}
