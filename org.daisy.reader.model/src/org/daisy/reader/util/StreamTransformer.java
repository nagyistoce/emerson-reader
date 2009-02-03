package org.daisy.reader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Perform rudimentary transforms of XML (primarily XHTML family) documents using javax.xml.stream.
 * @author Markus Gylling
 */
public class StreamTransformer {

	/**
	 * Transform an XML resource.
	 * 
	 * @param source
	 *            The XML file to transform
	 * @param destination
	 *            The result location
	 * @param config
	 *            A Map of configuration objects. The following keys are recognized:
	 * <dl>
	 *   <dt>KEY_OUTPUT_ENCODING</dt>
	 *   <dd>String. If null, utf-8 is used.</dd>
	 *   <dt>KEY_DTD</dt>
	 *   <dd>String. If null, input declaration is used.</dd>   
	 *   <dt>KEY_NAMESPACE_MAP</dt>
	 *   <dd>Map&lt;String,String&gt; where key is namespace URI to replace, and value is replacement URI</dd>   
	 *   <dt>KEY_ELEMENT_MAP</dt>
	 *   <dd>Map&lt;String,String&gt; where key is element local name to replace, and value is replacement local name</dd>
	 *   <dt>KEY_HTTP_EQUIV</dt>
	 *   <dd>Boolean. Whether to add HTML-style meta http-equiv encoding attribute as a child of an element head</dd>
	 *   <dt>KEY_MOD_RELATIVE_LINKS</dt>
	 *   <dd>Collection<String>. Whether to modify relative URLs occurring within the document to point back to source base.
	 *   The value is a collection of attribute names that may contain URLs. If the key is absent, or the value is null, no modification is done.</dd>
	 *   <dt>KEY_FORCE_HTML_EXTENSION</dt>
	 *   <dd>Boolean. Whether to force the extension ".html" to result. If null, no change made.</dd>
	 * </dl>          
	 * @throws IOException 
	 * @throws XMLStreamException 
	 * @throws URISyntaxException 
	 */
	
	public static File transform(URL source, File result, Map<String, Object> config) throws IOException, XMLStreamException, URISyntaxException {
		XMLInputFactory xif = null;
		XMLOutputFactory xof = null;
		InputStream is = null;
		XMLStreamReader reader = null;
		XMLStreamWriter writer = null;
				
		try {
			//get settings from config
			String encoding = (String)config.get(KEY_OUTPUT_ENCODING);
			if(encoding == null) encoding = UTF_8;			
			Map<?,?> nsMap = (Map<?,?>)config.get(KEY_NAMESPACE_MAP);
			Map<?,?> elemMap = (Map<?,?>)config.get(KEY_ELEMENT_MAP);
			Boolean addHttpEquiv = (config.containsKey(KEY_HTTP_EQUIV)) 
				? (Boolean)config.get(KEY_HTTP_EQUIV) 
				: Boolean.FALSE;
			boolean httpEquivAdded = false;
			
			//whether to mod relative links, null if not
			Collection<?> linkAttrs = (Collection<?>)config.get(KEY_MOD_RELATIVE_LINKS);
			
			//base, if modifying relative links
			String base = URIStringParser.getBase(source.toURI().toASCIIString());
			Pattern URI_REMOTE = Pattern.compile(
				"(^http:.+)|(^https:.+)|(^ftp:.+)|(^mailto:.+)|(^gopher:.+)|(^news:.+)|(^nntp:.+)|(^rtsp:.+)|(^bundleresource:.+)"); //$NON-NLS-1$
			
			String piCssUri = null;
			
			//extension
			Boolean ext = (Boolean)config.get(KEY_FORCE_HTML_EXTENSION);
			if(ext!=null && ext.booleanValue()) {
				result = new File(result.getParentFile(),FileUtils.getNameMinusExtension(result)+".html"); //$NON-NLS-1$
			}
						
			//set up reader and writer
			xif = StAXFactoryProxy.getXMLInputFactory();
			xof = StAXFactoryProxy.getXMLOutputFactory();
			is = source.openStream();
			reader = xif.createXMLStreamReader(is);
			writer = xof.createXMLStreamWriter(new FileOutputStream(result), encoding);
						
			writer.writeStartDocument(encoding, "1.0");
			while (reader.hasNext()) {
				reader.next();
				switch(reader.getEventType()) {
				    case XMLStreamConstants.START_ELEMENT:
				    	
				    	String elemName = reader.getLocalName();
				    	
				    	if(elemMap!=null && elemMap.containsKey(elemName))
				    		elemName = (String)elemMap.get(elemName);
				    	writer.writeStartElement(elemName);
				    	
						for (int i = 0; i < reader.getNamespaceCount(); i++) {		
							String nsURI = reader.getNamespaceURI(i);
							if(elemMap!=null && nsMap.containsKey(nsURI)) {
								nsURI = (String)nsMap.get(nsURI);
							}
							writer.writeNamespace(reader.getNamespacePrefix(i), nsURI);
						}
						
						for (int i = 0; i < reader.getAttributeCount(); i++) {	
							String attrName = reader.getAttributeLocalName(i);
							String attrValue = reader.getAttributeValue(i);
							
							if(linkAttrs!=null && linkAttrs.contains(attrName)
									&& !URI_REMOTE.matcher(attrValue).matches()) {
								attrValue = base + attrValue;
							}
							
							writer.writeAttribute(
									reader.getAttributePrefix(i),
									reader.getAttributeNamespace(i),
									attrName,
									attrValue);
						}
															
						if(!httpEquivAdded && addHttpEquiv && elemName.equals(ELEMENT_HEAD)) {
							//<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8"/>
							writer.writeStartElement(ELEMENT_META);
							writer.writeAttribute(ATTRIB_HTTP_EQUIV, CONTENT_TYPE);
							writer.writeAttribute(ATTRIB_CONTENT, "application/xhtml+xml; charset=utf-8"); //$NON-NLS-1$
							writer.writeEndElement();
							httpEquivAdded = true;
						}
						
						
						if(piCssUri!=null && elemName.equals(ELEMENT_HEAD)) {
							writer.writeEmptyElement(LINK);
							writer.writeAttribute(HREF, piCssUri);
							writer.writeAttribute(REL, STYLESHEET);
							writer.writeAttribute(TYPE, TEXT_CSS);
							piCssUri=null;
						}
						
						break;
					case XMLStreamConstants.DTD:
						String dtd = (String)config.get(KEY_DTD);
						if(dtd!=null)  {
							writer.writeDTD(dtd);
						}else{
							writer.writeDTD(reader.getText());
						}
						break;						
					case XMLStreamConstants.CHARACTERS:
						writer.writeCharacters(reader.getTextCharacters(), 
								reader.getTextStart(), reader.getTextLength());		
						break;
					case XMLStreamConstants.NAMESPACE:	
						writer.writeNamespace(reader.getPrefix(), reader.getNamespaceURI());
						break;	
					case XMLStreamConstants.PROCESSING_INSTRUCTION:	
						//<?xml-stylesheet href="dtbook.2005.basic.css" type="text/css"?>
						String data = reader.getPIData();
						//presence of linkAttrs mean that modrelative is active
						if(linkAttrs!=null) {
							data = data.replace('\"', '\'');
							data = data.replace("href='", "href='"+base); //$NON-NLS-1$ //$NON-NLS-2$
						}
						boolean forward = true;
						if(reader.getPITarget().equals(XML_STYLESHEET)
								&& data.contains(CSS)) {
							try{
								char ch = '\'';
								int startIndex = data.indexOf(ch)+1;
								int endIndex = data.indexOf(ch,startIndex+1);
								piCssUri = data.substring(startIndex, endIndex);
								//forward = false;
							}catch (Exception e) {
								piCssUri = null;
							}	
						}
						if(forward)
							writer.writeProcessingInstruction(reader.getPITarget(), data);										
						break;					
					case XMLStreamConstants.END_ELEMENT:
						writer.writeEndElement();
						break;
					case XMLStreamConstants.END_DOCUMENT:
						writer.writeEndDocument();	
						break;							
					default:
						break;
				} //switch			
			}
		} finally {
			if (reader != null)
				reader.close();
			if (writer != null) {
				writer.flush();
				writer.close();
			}	
			if (is != null)
				is.close();
		}

		return result;
	}

	public static final String KEY_OUTPUT_ENCODING = "KEY_OUTPUT_ENCODING"; //$NON-NLS-1$
	public static final String KEY_DTD = "KEY_DTD"; //$NON-NLS-1$
	public static final String KEY_NAMESPACE_MAP = "KEY_NAMESPACE_MAP"; //$NON-NLS-1$
	public static final String KEY_ELEMENT_MAP = "KEY_ELEMENT_MAP"; //$NON-NLS-1$
	public static final String KEY_HTTP_EQUIV = "KEY_HTTP_EQUIV"; //$NON-NLS-1$
	public static final String KEY_MOD_RELATIVE_LINKS ="KEY_MOD_RELATIVE_LINKS"; //$NON-NLS-1$
	public static final String KEY_FORCE_HTML_EXTENSION="KEY_FORCE_HTML_EXTENSION"; //$NON-NLS-1$
	
	public static final String NAMESPACE_DTBOOK = "http://www.daisy.org/z3986/2005/dtbook/"; //$NON-NLS-1$
	public static final String NAMESPACE_XHTML = "http://www.w3.org/1999/xhtml"; //$NON-NLS-1$
	
	public static final String ELEMENT_HTML = "html"; //$NON-NLS-1$
	public static final String ELEMENT_BOOK = "book"; //$NON-NLS-1$
	public static final String ELEMENT_BODY = "body"; //$NON-NLS-1$
	public static final String ELEMENT_DTBOOK = "dtbook"; //$NON-NLS-1$
	
	private static final String ELEMENT_META = "meta"; //$NON-NLS-1$
	private static final String ELEMENT_HEAD = "head"; //$NON-NLS-1$
	private static final String ATTRIB_HTTP_EQUIV = "http-equiv"; //$NON-NLS-1$
	private static final String ATTRIB_CONTENT ="content"; //$NON-NLS-1$
	private static final String CONTENT_TYPE ="Content-Type"; //$NON-NLS-1$
	
	private static final String CSS = "css"; //$NON-NLS-1$
	private static final String XML_STYLESHEET = "xml-stylesheet"; //$NON-NLS-1$
	private static final String REL = "rel"; //$NON-NLS-1$
	private static final String LINK = "link"; //$NON-NLS-1$
	private static final String STYLESHEET = "stylesheet"; //$NON-NLS-1$
	private static final String HREF = "href"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$
	private static final String TEXT_CSS = "text/css"; //$NON-NLS-1$

	private static final String UTF_8 = "utf-8"; //$NON-NLS-1$
	
	
}
