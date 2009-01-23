package org.daisy.reader.model.z2005;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.smil.ISmilLoader;
import org.daisy.reader.model.smil.SmilSpine;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.util.StAXFactoryProxy;
import org.daisy.reader.util.URLUtils;


/*
 * Adopt a lazy loading approach as to minimize time elapsed until
 * returning.
 * 
 * The idea is to parse the OPF, NCX and populate
 * SMIL file proxies, then return, parse subsequent SMIL files as 
 * they are requested. 
 */
final class Z2005ModelLoader {
			
	static Z2005Model load(URL opf) throws Exception {

		Ncx navigation = new Ncx();				
		SmilSpine spine = new SmilSpine();
		Metadata metadata = new Metadata(opf);
		
		//create an ISmilLoader to pass into to SmilFile objects (lazy parsing)
		ISmilLoader smilLoader = new Z2005SmilLoader();
		
		/*
		 * Read the opf, populate navigation and spine objects.
		 * The entire opf and ncx are parsed instantly, but the SmilSpines
		 * SmilFiles are lazy and uses proxied accessors.
		 * 
		 * We do not spend time here to check whether referenced
		 * SMIL files actually exist etc. The default assumption
		 * is that the DTB is in an OK state.
		 */
	
		DefaultOpfReader.read(opf,navigation,spine,metadata,smilLoader);
//		//set the initial audio position
//		spine.getAudioClipCursor().set(spine.getFirstAudioMediaObject());
		return new Z2005Model(opf,navigation,metadata,spine);		

	}
	
	/**
	 * A worker class that builds the Z2005Navigation and SmilSpine objects using
	 * a StAX-parse of the input OPF.
	 */
	static final class DefaultOpfReader {

		public static void read(URL opfURL, Ncx navigation, SmilSpine spine, Metadata metadata, ISmilLoader smilLoader) throws Exception {			
			InputStream inputStream = opfURL.openStream();
			XMLStreamReader reader = StAXFactoryProxy.getXMLInputFactory().createXMLStreamReader(inputStream);			
			Map<String,String> smilMap = new HashMap<String,String>(); //manifestID, smilURL
			boolean ncxFound = false;
			boolean inMetadata = false;
			String uidIdref = null;
			//Set<String> encounteredUIDs = new HashSet<String>();
	    	try{	    		
		    	while(reader.hasNext()) {
		    		reader.next();
		    		if(reader.isStartElement()) {
		    			if(reader.getLocalName()==itemElem) {
		    				//we have a manifest/item
		    				if(isSmilItem(reader)) {
		    					String id = reader.getAttributeValue(null, idAttr);
		    					String smilRef = reader.getAttributeValue(null, hrefAttr);		    					
		    					smilMap.put(id, smilRef);
		    				}else if(!ncxFound && isNcxItem(reader)) {
		    					String ncxRef = reader.getAttributeValue(null, hrefAttr);
		    					URL ncxURL = URLUtils.resolve(opfURL, ncxRef);
		    					navigation.load(ncxURL); 
		    					ncxFound = true;
		    				}
		    			}else if(reader.getLocalName()==itemRefElem) {
		    				//we have a spine/itemref
		    				String idref = reader.getAttributeValue(null, idRefAttr);
		    				if(idref!=null) {
			    				String ref = smilMap.get(idref);
			    				if(ref!=null) {
			    					spine.add(ref, opfURL, smilLoader);
			    					continue;
			    				}			    				
		    				}		    				
		    				Activator.getDefault().logError("Missing smil reference in OPF",  //$NON-NLS-1$
		    						new NullPointerException(idref));
		    			}else if(reader.getLocalName()==metadataElem) {
		    				inMetadata = true;
		    			}else if(reader.getLocalName()==packageElem) {
		    				uidIdref = reader.getAttributeValue(null, uniqueIdentifierAttr);
		    			}else if(inMetadata) {
		    				String elemName = reader.getLocalName().toLowerCase();
		    				if(elemName.equals(dcIdentifierLocalName)) {
		    					//String uid = reader.getElementText();
		    					//encounteredUIDs.add(uid);		    					
		    					String id = reader.getAttributeValue(null, idAttr);
		    				    if(uidIdref!=null && id!=null &&  id.equals(uidIdref))	
		    				    	metadata.put(Metadata.UUID, reader.getElementText());
		    				} else
		    				if(elemName.equals(dcCreatorLocalName)) {
			    				metadata.put(Metadata.AUTHOR, reader.getElementText());
			    			} else
			    			if(elemName.equals(dcTitleLocalName)) {
			    				metadata.put(Metadata.TITLE, reader.getElementText());
			    			}else
				    		if(elemName.equals(metaElem)) {
				    			String name = reader.getAttributeValue(null, nameAttr);
				    			if(name.toLowerCase().equals(dtbTotalTimeName)) {
				    				spine.setDuration(reader.getAttributeValue(null, contentAttr));
				    			}	
				    		}
		    				
		    			}
		    		} else if(reader.isEndElement()) {
		    			if(reader.getLocalName()==metadataElem) {
		    				inMetadata = false;
		    			}
		    		}
		    	}
	    	} catch (XMLStreamException e) {
	    		throw e;
	    	} finally {
//	    		if(!metadata.containsKey(Metadata.UUID) && !encounteredUIDs.isEmpty()) {
//	    			metadata.put(Metadata.UUID, encounteredUIDs.iterator().next());
//	    		}
	    		if(reader!=null)reader.close();
	    		if(inputStream!=null)inputStream.close();
	    	}			
		}
	}
	
	public static boolean isNcxItem(XMLStreamReader reader) {
		//ignore mimetype as it changed between 2002 & 2005
		//we hope to use this model as-is for 2002 as well
		String value = reader.getAttributeValue(null, idAttr);		
		if(value.equals("ncx")) return true;		 //$NON-NLS-1$
		value = reader.getAttributeValue(null, hrefAttr);
		if(value.matches(".+\\.[Nn][Cc][Xx]")) return true; //$NON-NLS-1$
		return false;
	}

	public static boolean isSmilItem(XMLStreamReader reader) {
		String value =  reader.getAttributeValue(null, mediaTypeAttr);
		if(value!=null && value.equals(smilMime)) return true;
		value = reader.getAttributeValue(null, hrefAttr);
		if(value!=null && value.matches(".+\\.[Ss][Mm][Ii][Ll]")) return true; //$NON-NLS-1$
		return false;
	}
	
	private static final String itemElem = "item"; //$NON-NLS-1$
	private static final String metadataElem = "metadata"; //$NON-NLS-1$
	private static final String packageElem = "package"; //$NON-NLS-1$
	private static final String uniqueIdentifierAttr = "unique-identifier"; //$NON-NLS-1$
	private static final String itemRefElem = "itemref";	 //$NON-NLS-1$
	private static final String idRefAttr = "idref"; //$NON-NLS-1$
	private static final String hrefAttr = "href"; //$NON-NLS-1$
	private static final String idAttr = "id"; //$NON-NLS-1$
	private static final String smilMime = "application/smil"; //$NON-NLS-1$
	private static final String mediaTypeAttr = "media-type"; //$NON-NLS-1$
	private static final String dcIdentifierLocalName = "identifier"; //$NON-NLS-1$
	private static final String dcCreatorLocalName = "creator"; //$NON-NLS-1$
	private static final String dcTitleLocalName = "title"; //$NON-NLS-1$
	private static final String metaElem = "meta"; //$NON-NLS-1$
	private static final String nameAttr = "name"; //$NON-NLS-1$
	private static final String contentAttr = "content"; //$NON-NLS-1$
	private static final String dtbTotalTimeName = "dtb:totaltime"; //$NON-NLS-1$
	
}
