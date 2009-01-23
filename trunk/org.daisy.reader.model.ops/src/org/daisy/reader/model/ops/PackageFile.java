package org.daisy.reader.model.ops;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.util.StAXFactoryProxy;

/**
 * Represent the OPF.
 * @author Markus Gylling
 */
public class PackageFile  {
	
	static final String MIMETYPE = "application/oebps-package+xml"; //$NON-NLS-1$
	String mUID = null;
	String mTitle = null;
	String mCreator = null;
	String mPublisher = null;
	Set<PackageFileItem> mManifest = null;
	List<PackageFileItem> mSpine = null;
	PackageFileItem mNCX = null;
	URL mURL = null;
	
	private char[] mPackageFileParent = null;
	
	PackageFile(URL path) throws XMLStreamException, IOException, URISyntaxException {
		
		mManifest = new HashSet<PackageFileItem>();
		mSpine = new ArrayList<PackageFileItem>();				
		mURL = path;
		String s = mURL.toString();
		mPackageFileParent = s.substring(0, s.lastIndexOf('/')+1).toCharArray();
					
		XMLInputFactory xif = StAXFactoryProxy.getXMLInputFactory();
		XMLStreamReader reader = null;
		try{							
			reader = xif.createXMLStreamReader(path.openStream());
			read(reader);						
		}finally{
			if(reader!=null)reader.close();
		}
	}
	
	private void read(XMLStreamReader reader) throws XMLStreamException {	
						
		String uidref = null;
		String identifierCache = null;
		boolean inManifest = false;
		boolean inSpine = false;
		
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement()) {
				if (reader.getNamespaceURI().equals(opfNsUri)) {
					if(reader.getLocalName().equals(SPINE)) {
						inSpine = false;
					}else if(reader.getLocalName().equals(MANIFEST)) {
						inManifest = false;	
					}
				}
			}else if(reader.isStartElement()) {
				if (reader.getNamespaceURI().equals(opfNsUri)) {
					if(reader.getLocalName().equals(PACKAGE)) {
						uidref = reader.getAttributeValue(null, UNIQUE_IDENTIFIER);	
					}else if(reader.getLocalName().equals(ITEM) && inManifest) {
						try {
							mManifest.add(new PackageFileItem(
									reader.getAttributeValue(null, ID),
									reader.getAttributeValue(null, HREF),
									mPackageFileParent,
									reader.getAttributeValue(null, MEDIA_TYPE)));
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}else if(reader.getLocalName().equals(SPINE)) {
						inSpine = true;
						mNCX = getItem(reader.getAttributeValue(null, TOC));
					}else if(reader.getLocalName().equals(MANIFEST)) {
						inManifest = true;	
					}else if(reader.getLocalName().equals(ITEMREF) && inSpine) {
						PackageFileItem item = getItem(reader.getAttributeValue(null, IDREF));
						if(item!=null)
							mSpine.add(item);
					}				
				}else if (reader.getNamespaceURI().equals(dcNsUri)) {
					if(reader.getLocalName().equals(METADATA)) {
						continue;
					}else if(reader.getLocalName().equals(TITLE)) { 					
						mTitle = reader.getElementText();						
					}else if(reader.getLocalName().equals(CREATOR)){
						mCreator = reader.getElementText();						
					}else if(reader.getLocalName().equals(PUBLISHER)){
						mPublisher = reader.getElementText();						
					}else if(reader.getLocalName().equals(TITLE)){ 						
						mTitle = reader.getElementText();						
					}else if(reader.getLocalName().equals(IDENTIFIER)) {
						if(reader.getAttributeValue(null, ID).equals(uidref)) {
							mUID = reader.getElementText();
							//temporary fix for ggl
							if(!uidref.equals("uid")) //$NON-NLS-1$
							  identifierCache = uidref;
						}else{
							identifierCache = reader.getElementText();
						}
						continue;
					}
				}
			}
		}
		
		if(mUID==null||mUID.length()<1)
			mUID = identifierCache;
	}
	
	private PackageFileItem getItem(String identity) {
		for(PackageFileItem item : mManifest) {
			if(item.mItemID.equals(identity))
				return item;
		}
		return null;
	}
				
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("uid: ").append(mUID==null?"null":mUID.toString()).append("\n");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		sb.append("title: ").append(mTitle==null?"null":mTitle.toString()).append("\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		sb.append("creator: ").append(mCreator==null?"null":mCreator.toString()).append("\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		sb.append("publisher: ").append(mPublisher==null?"null":mPublisher.toString()).append("\n");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		sb.append("url: ").append(mURL==null?"null":mURL.toString()).append("\n");				 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sb.append("Manifest:").append("\n");  //$NON-NLS-1$//$NON-NLS-2$
		for (PackageFileItem item : mManifest) {
			sb.append("\t").append(item.toString()).append("\n");  //$NON-NLS-1$//$NON-NLS-2$
		}		
		sb.append("Spine:").append("\n");  //$NON-NLS-1$//$NON-NLS-2$
		for (PackageFileItem item : mSpine) {
			sb.append("\t").append(item.toString()).append("\n");  //$NON-NLS-1$//$NON-NLS-2$
		}
		sb.append("ncx: ").append(mNCX==null?"null":mNCX.toString()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		return sb.toString();		
	}
	
	private static final String IDENTIFIER = "identifier"; //$NON-NLS-1$
	private static final String PUBLISHER = "publisher"; //$NON-NLS-1$
	private static final String CREATOR = "creator"; //$NON-NLS-1$
	private static final String IDREF = "idref"; //$NON-NLS-1$
	private static final String ITEMREF = "itemref"; //$NON-NLS-1$
	private static final String TITLE = "title"; //$NON-NLS-1$
	private static final String METADATA = "metadata"; //$NON-NLS-1$
	private static final String TOC = "toc"; //$NON-NLS-1$
	private static final String MEDIA_TYPE = "media-type"; //$NON-NLS-1$
	private static final String HREF = "href"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String ITEM = "item"; //$NON-NLS-1$
	private static final String UNIQUE_IDENTIFIER = "unique-identifier"; //$NON-NLS-1$
	private static final String PACKAGE = "package"; //$NON-NLS-1$
	private static final String MANIFEST = "manifest"; //$NON-NLS-1$
	private static final String SPINE = "spine"; //$NON-NLS-1$
	private static final String opfNsUri = "http://www.idpf.org/2007/opf"; //$NON-NLS-1$
	private static final String dcNsUri = "http://purl.org/dc/elements/1.1/"; //$NON-NLS-1$

}
