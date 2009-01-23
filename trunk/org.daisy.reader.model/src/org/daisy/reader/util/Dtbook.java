package org.daisy.reader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class Dtbook {
	
	/**
	 * HTML-ize a dtbook file for browser compatibility purposes.
	 * <p>The process includes:</p>
	 * <ul>
	 * <li>Change of default namespace</li>
	 * <li>Change of root element name</li>
	 * <li>adding xml:base and head/base@href</li>
	 * </ul>
	 * @param source The source DTBook File
	 * @param dest The file to stream to
	 * @param modifyRelativeLinks Whether to modify relative links to point to the source location
	 * @return The created File
	 * @throws Exception 
	 */
		
	public static File htmlize(URL source, File dest, boolean modifyRelativeLinks) throws IOException, XMLStreamException {
		XMLInputFactory xif = null;			
		XMLOutputFactory xof = null;
		InputStream is = null;
		XMLStreamReader reader = null;
		XMLStreamWriter writer = null;
					
		try{
						
			String base = URIStringParser.getBase(source.getPath());
			String cssURI = null;
			
			xif = StAXFactoryProxy.getXMLInputFactory();
			xof = StAXFactoryProxy.getXMLOutputFactory();
			is = source.openStream();
			reader = xif.createXMLStreamReader(is);			
			writer = xof.createXMLStreamWriter(new FileOutputStream(dest),UTF_8); 
			
			boolean firstStartElement = true;
			//String defaultNamespace = "";
			String defaultNamespace = XHTML_NS; 
			writer.writeStartDocument(UTF_8, "1.0");			 //$NON-NLS-1$
			while(reader.hasNext()) {				
				reader.next();
				switch(reader.getEventType()) {
					case XMLStreamConstants.END_DOCUMENT:
						writer.writeEndDocument();	
						break;
					case XMLStreamConstants.ATTRIBUTE:							
						if(reader.getNamespaceURI().equals(defaultNamespace)) {
							writer.writeAttribute(reader.getLocalName(),reader.getText());
						}else{
							writer.writeAttribute(reader.getPrefix(),reader.getNamespaceURI(),reader.getLocalName(),reader.getText());
						}	
						break;
					case XMLStreamConstants.CHARACTERS:
						writer.writeCharacters(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
						break;
					case XMLStreamConstants.START_ELEMENT:
						writer.writeStartElement(mod(reader.getLocalName()));

						if(firstStartElement) {
							//defaultNamespace = reader.getNamespaceURI();
							writer.writeDefaultNamespace(defaultNamespace);
							if(!modifyRelativeLinks)
								writer.writeAttribute(XML_BASE, base);
						}	
						firstStartElement = false;		
						
						boolean isImage = reader.getLocalName()==IMG;

						for (int i = 0; i < reader.getAttributeCount(); i++) {
							if(reader.getAttributeNamespace(i)!=null 
									&& reader.getAttributeNamespace(i).equals(defaultNamespace)) {
								if(modifyRelativeLinks && isImage && reader.getAttributeLocalName(i)==SRC) {
									writer.writeAttribute(
											reader.getAttributeLocalName(i),
											base + reader.getAttributeValue(i));
								}else{
									writer.writeAttribute(
										reader.getAttributeLocalName(i),
										reader.getAttributeValue(i));
								}
							}else{
								if(modifyRelativeLinks && isImage && reader.getAttributeLocalName(i)==SRC) {
									writer.writeAttribute(
											reader.getAttributeLocalName(i),
											base + reader.getAttributeValue(i));
								}else{
									writer.writeAttribute(
										reader.getAttributePrefix(i),
										reader.getAttributeNamespace(i),
										reader.getAttributeLocalName(i),
										reader.getAttributeValue(i));
								}	
							}
						}
						
						if(reader.getName().getLocalPart().equals(HEAD)) {
							if(!modifyRelativeLinks) {
								writer.writeEmptyElement(BASE);
								writer.writeAttribute(HREF, base);
							}	
							if(modifyRelativeLinks && cssURI!=null) {
									writer.writeEmptyElement(LINK);
									writer.writeAttribute(HREF, cssURI);
									writer.writeAttribute(REL, STYLESHEET);
									writer.writeAttribute(TYPE, TEXT_CSS);
							}
							
						}
						
						break;
					case XMLStreamConstants.END_ELEMENT:
						writer.writeEndElement();	
						break;
					case XMLStreamConstants.CDATA:	
						writer.writeCData(reader.getElementText());
						break;
					case XMLStreamConstants.NAMESPACE:	
						writer.writeNamespace(reader.getPrefix(), reader.getNamespaceURI());
						break;
					case XMLStreamConstants.PROCESSING_INSTRUCTION:	
						//<?xml-stylesheet href="dtbook.2005.basic.css" type="text/css"?>
						String data = reader.getPIData();
						if(modifyRelativeLinks) {
							data = data.replace('\"', '\'');
							data = data.replace("href='", "href='"+base); //$NON-NLS-1$ //$NON-NLS-2$
								
							if(reader.getPITarget().equals(XML_STYLESHEET)
									&& data.contains(CSS)) {
								char ch = '\'';
								int startIndex = data.indexOf(ch)+1;
								int endIndex = data.indexOf(ch,startIndex+1);
								cssURI = data.substring(startIndex, endIndex);
							}
						}
						writer.writeProcessingInstruction(reader.getPITarget(), data);
						break;
					default:	
						//System.err.println("skipping event type " + eventType);
						break;
				}
			}		
		}finally{			
			if(reader!=null) reader.close();
			if(writer!=null) writer.flush(); writer.close();
			if(is!=null) is.close();
		}		
		return dest;
	}
	
	public static File htmlize(URL source, File dest) throws IOException, XMLStreamException {
		return Dtbook.htmlize(source, dest, false);
	}
	
	private static String mod(String localName) {
		if(localName==DTBOOK) return HTML;
		if(localName==BOOK) return BODY;
		return localName;
	}
	
	private static final String TYPE = "type"; //$NON-NLS-1$
	private static final String TEXT_CSS = "text/css"; //$NON-NLS-1$
	private static final String HTML = "html"; //$NON-NLS-1$
	private static final String BOOK = "book"; //$NON-NLS-1$
	private static final String BODY = "body"; //$NON-NLS-1$
	private static final String DTBOOK = "dtbook"; //$NON-NLS-1$
	private static final String CSS = "css"; //$NON-NLS-1$
	private static final String XML_STYLESHEET = "xml-stylesheet"; //$NON-NLS-1$
	private static final String REL = "rel"; //$NON-NLS-1$
	private static final String LINK = "link"; //$NON-NLS-1$
	private static final String STYLESHEET = "stylesheet"; //$NON-NLS-1$
	private static final String HREF = "href"; //$NON-NLS-1$
	private static final String BASE = "base"; //$NON-NLS-1$
	private static final String HEAD = "head"; //$NON-NLS-1$
	private static final String SRC = "src"; //$NON-NLS-1$
	private static final String IMG = "img"; //$NON-NLS-1$
	private static final String XML_BASE = "xml:base"; //$NON-NLS-1$
	private static final String XHTML_NS = "http://www.w3.org/1999/xhtml"; //$NON-NLS-1$
	private static final String UTF_8 = "utf-8"; //$NON-NLS-1$
}
