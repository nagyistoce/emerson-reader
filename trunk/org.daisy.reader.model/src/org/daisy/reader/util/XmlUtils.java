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

public class XmlUtils {
	private static final String UTF_8 = "utf-8"; //$NON-NLS-1$
	
	public static File stripDocType(URL source, File dest) throws IOException, XMLStreamException {
		XMLInputFactory xif = null;			
		XMLOutputFactory xof = null;
		InputStream is = null;
		XMLStreamReader reader = null;
		XMLStreamWriter writer = null;
					
		try{
									
			xif = StAXFactoryProxy.getXMLInputFactory();
			xof = StAXFactoryProxy.getXMLOutputFactory();
			is = source.openStream();
			reader = xif.createXMLStreamReader(is);			
			writer = xof.createXMLStreamWriter(new FileOutputStream(dest),UTF_8); 
			while(reader.hasNext()) {
				reader.next();
				switch(reader.getEventType()) {
					case XMLStreamConstants.DTD:
						break;
					case XMLStreamConstants.START_DOCUMENT:
						writer.writeStartDocument(reader.getCharacterEncodingScheme(), reader.getVersion());
						break;
					case XMLStreamConstants.END_DOCUMENT:
						writer.writeEndDocument();
						break;
					case XMLStreamConstants.START_ELEMENT:
						writer.writeStartElement(reader.getPrefix(),reader.getLocalName(),reader.getNamespaceURI());						
						for (int i = 0; i < reader.getAttributeCount(); i++) {
							writer.writeAttribute(reader.getAttributePrefix(i),
									reader.getAttributeNamespace(i), 
										reader.getAttributeLocalName(i), 
											reader.getAttributeValue(i));							
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						writer.writeCharacters(reader.getTextCharacters(), 
								reader.getTextStart(), reader.getTextLength());		
						break;
					case XMLStreamConstants.END_ELEMENT:
						writer.writeEndElement();
						break;
					case XMLStreamConstants.NAMESPACE:	
						writer.writeNamespace(reader.getPrefix(), reader.getNamespaceURI());
						break;		
					case XMLStreamConstants.PROCESSING_INSTRUCTION:	
						writer.writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
					default:
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
}
