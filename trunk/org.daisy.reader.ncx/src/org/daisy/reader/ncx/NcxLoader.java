package org.daisy.reader.ncx;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.util.StAXFactoryProxy;

public class NcxLoader {
	
	public static void load(Ncx ncx) throws IOException, XMLStreamException {
		InputStream inputStream = ncx.getSourceURL().openStream();
		XMLStreamReader reader = StAXFactoryProxy.getXMLInputFactory().createXMLStreamReader(inputStream);
							
		try {
			
			while(reader.hasNext()) {
				reader.next();
				
				//for now, support only headings and pages
				if(reader.isStartElement() && reader.getLocalName()==navMapElem) {
					ncx.navMap = readNavMap(reader, ncx.navMap);
				}				
				else if(reader.isStartElement()&& reader.getLocalName()==pageListElem) {
					ncx.navLists.add(readPageList(reader));
				}
				else if(reader.isStartElement()&& reader.getLocalName()==ncxCustomTestElem) {
					NcxCustomTest ct = readCustomTest(reader);
					ncx.ncxCustomTestData.put(ct.getId(), ct);
				}
				
			}	
						
			
		} finally {
			if(reader!=null)reader.close();
			if(inputStream!=null)inputStream.close();
		}
	}
		
	private static NcxCustomTest readCustomTest(XMLStreamReader reader) {		
		return new NcxCustomTest(
				reader.getAttributeValue(null, bookStructAttr),
					Boolean.parseBoolean(reader.getAttributeValue(null, defaultStateAttr)),
						reader.getAttributeValue(null, idAttr),
							reader.getAttributeValue(null, overrideAttr)
				);
	}
	
	private static NcxNavMap readNavMap(XMLStreamReader reader, NcxNavMap navMap) throws XMLStreamException {
		//the reader is positioned at a navMap startelement
				 				
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName()==navMapElem) {
				break;
			}			
			if(reader.isStartElement() && reader.getLocalName()==navPointElem) {				
				navMap.children.add(readNavPoint(reader, navMap));
			}
		}	
		return navMap;
	}
	
	private static NcxNavPoint readNavPoint(XMLStreamReader reader, Object parent) throws XMLStreamException {
		//mNavPointCount++;
		//the reader is positioned at a navPoint startelement
		
		NcxNavPoint newItem = null;
		
		int playOrder = -1;
		String target = null;
		String label = null;
		
		try{
			playOrder = Integer.parseInt(reader.getAttributeValue(null, playOrderAttr));
		}catch (Exception e) {
			//hack-fix for nonexisting playorder attrs
			Activator.getDefault().logError(Messages.NcxLoader_noPlayOrderError, e);
			playOrder++;
		}	
		while(reader.hasNext()) {			
			reader.next();			
			if(reader.isEndElement() && reader.getLocalName()==navPointElem) {
				break;
			}else if (reader.isStartElement()) {
				if(reader.getLocalName()==contentElem) {
					target = reader.getAttributeValue(null, srcAttr);	
				}else if(reader.getLocalName()==textElem) {
					label = reader.getElementText();					
				}else if(reader.getLocalName()==navPointElem) {
					if(newItem == null)
						newItem = new NcxNavPoint(parent,label,target,playOrder);
					newItem.children.add(readNavPoint(reader,newItem));						
				}	
			}			
		}
		if(newItem == null) 
			newItem = new NcxNavPoint(parent,label,target,playOrder);		
		
		return newItem;
	}
	
	private static NcxNavList readPageList(XMLStreamReader reader) throws XMLStreamException {	
		//reader positioned at the pageList startElement
		NcxNavList pageList = new NcxNavList(Semantic.PAGE_NUMBER);
				
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName()==pageListElem) {
				break;
			}			
			if(reader.isStartElement() && reader.getLocalName()==pageTargetElem) {
				pageList.add(readPageTarget(reader, pageList));
			}
		}	
		return pageList;		
	}
	
	private static NcxPageTarget readPageTarget(XMLStreamReader reader, NcxNavList pageList) throws XMLStreamException {
		//the reader is positioned at a pageTarget startelement
		int playOrder = -1;
		//int value = -1;
		//String type = null;
		String target = null;
		String label = null;
		
		try{
			playOrder = Integer.parseInt(reader.getAttributeValue(null, playOrderAttr));
		}catch (Exception e) {
			//hack-fix for nonexisting playorder attrs
			Activator.getDefault().logError(Messages.NcxLoader_noPlayOrderError, e);
			playOrder++;
		}
				
		while(reader.hasNext()) {
			reader.next();
			if(reader.isEndElement() && reader.getLocalName()==pageTargetElem) {
				break;
			}else if (reader.isStartElement()) {
				if(reader.getLocalName()==contentElem) {
					target = reader.getAttributeValue(null, srcAttr);	
				}else if(reader.getLocalName()==textElem) {
					label = reader.getElementText(); //TODO collect chars for speed
				}
			}			
		}
		return new NcxPageTarget(pageList,label,target,playOrder);
	}
		
	private static final String navMapElem = "navMap"; //$NON-NLS-1$
	private static final String navPointElem = "navPoint"; //$NON-NLS-1$
	private static final String contentElem = "content"; //$NON-NLS-1$
	private static final String textElem = "text"; //$NON-NLS-1$
	private static final String pageListElem = "pageList"; //$NON-NLS-1$
	private static final String pageTargetElem = "pageTarget"; //$NON-NLS-1$
	private static final String ncxCustomTestElem = "smilCustomTest"; //$NON-NLS-1$
	private static final String srcAttr = "src"; //$NON-NLS-1$
	private static final String playOrderAttr = "playOrder"; //$NON-NLS-1$
	private static final String bookStructAttr = "bookStruct"; //$NON-NLS-1$
	private static final String defaultStateAttr = "defaultState"; //$NON-NLS-1$
	private static final String idAttr = "id"; //$NON-NLS-1$
	private static final String overrideAttr = "override"; //$NON-NLS-1$

}