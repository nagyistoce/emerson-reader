package org.daisy.reader.util;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

/**
 * Utility class to circumvent the sporadic NPEs ocurring when
 * using Woodstox StartElement.getAttributeByName() 
 * @author Markus Gylling
 */
public class AttributeByName {

	public static Attribute get(QName name, StartElement se) {
		Iterator<?> attributes = se.getAttributes();
		Attribute a;
		while(attributes.hasNext()) {			
			a = (Attribute)attributes.next();
			if(name.equals(a.getName())) return a;			
		}
		return null;
	}
	
	public static String getValue(QName name, XMLStreamReader reader) {
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			if(reader.getAttributeName(i).equals(name)) {
				return reader.getAttributeValue(i);
			}
		}		
		return null;
	}
}
