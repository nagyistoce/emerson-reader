package org.daisy.reader.util;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxEventFactory;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * 
 * @author Markus Gylling
 */
public class StAXFactoryProxy {
	private static XMLInputFactory inputFactory;
	private static XMLEventFactory eventFactory;
	private static XMLOutputFactory outputFactory;
	
	public static XMLInputFactory getXMLInputFactory() {
		if(inputFactory==null) {			
			inputFactory = new WstxInputFactory();
			((WstxInputFactory)inputFactory).configureForSpeed();
			((WstxInputFactory)inputFactory).setProperty(
					WstxInputProperties.P_UNDECLARED_ENTITY_RESOLVER, 
						UndeclaredEntityResolver.getInstance());
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
			inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);			
			inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
		}
		return inputFactory;
	}
	
	public static XMLEventFactory getXMLEventFactory() {		
		if(eventFactory==null) {				 
			eventFactory = new WstxEventFactory();			
		}
		return eventFactory;
	}
	
	public static XMLOutputFactory getXMLOutputFactory() {		
		if(outputFactory==null) {				 
			outputFactory = new WstxOutputFactory();			
		}
		return outputFactory;
	}
	
	
}