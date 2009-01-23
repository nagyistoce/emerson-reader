package org.daisy.reader.model.d202;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.ISmilLoader;
import org.daisy.reader.model.smil.ParContainer;
import org.daisy.reader.model.smil.SeqContainer;
import org.daisy.reader.model.smil.SmilFile;
import org.daisy.reader.model.smil.TextMediaObject;
import org.daisy.reader.model.smil.TimeContainer;
import org.daisy.reader.util.StAXFactoryProxy;

public class D202SmilLoader implements ISmilLoader {

	public boolean load(URL source, SmilFile smilFile) {
		
		try{
			return DefaultSmilReader.read(source,smilFile);
		}catch (XMLStreamException e) {
			//the smil was probably malformed, try again
			Activator.getDefault().logError(e.getMessage(), e);		
			try{
				return MalformedSmilReader.read(source,smilFile);
			}catch (XMLStreamException ee) {
				Activator.getDefault().logError(ee.getMessage(), ee);
			}
		}catch (IOException ioe) {
			Activator.getDefault().logError(ioe.getMessage(), ioe);
		}
		return false;
	}
	
	static final class DefaultSmilReader {
		
		static boolean read(URL url, SmilFile file) throws XMLStreamException, IOException {
						
			InputStream inputStream = url.openStream();
			XMLStreamReader reader = StAXFactoryProxy.getXMLInputFactory().createXMLStreamReader(inputStream);
								
			try {
				TimeContainer currentTimeContainer = null;
				String idValue = null;
				String skippable = null;
				
				while (reader.hasNext()) {
					reader.next();
		    		if(reader.isStartElement()) {
		    			
		    			idValue = reader.getAttributeValue(null, idAttr);
		    			
		    			if(reader.getLocalName()==audioElem) {		
		    				currentTimeContainer.addMediaObject(
		    						new AudioMediaObject(
		    								idValue, 
		    								reader.getAttributeValue(null, srcAttr),
		    								reader.getAttributeValue(null, clipBeginAttr),
		    								reader.getAttributeValue(null, clipEndAttr),
		    								currentTimeContainer));		    				
		    			
		    			}else if(reader.getLocalName()==parElem) {
		    				ParContainer pc = new ParContainer(idValue, currentTimeContainer, file);
		    				currentTimeContainer.addChildContainer(pc);
		    				currentTimeContainer = pc;
		    				
		    				skippable = reader.getAttributeValue(null, systemRequiredAttr);
		    				
		    				if(skippable!=null) {
		    					Semantic sem = null;
		    					if(skippable.equals(pagenumOnAttr)) {
		    						sem = Semantic.PAGE_NUMBER;
		    					}else if(skippable.equals(prodnoteOnAttr)) {
		    						sem = Semantic.OPTIONAL_PRODUCER_NOTE;
		    					}else if(skippable.equals(sidebarOnAttr)) {
		    						sem = Semantic.OPTIONAL_SIDEBAR;
		    					}else if(skippable.equals(footnoteOnAttr)) {
		    						sem = Semantic.NOTE;
		    					}
		    					pc.setSemantic(sem);
		    					skippable = null;
		    				}
		    				
		    			}else if(reader.getLocalName()==textElem) {
		    				currentTimeContainer.addMediaObject(
		    						new TextMediaObject(
		    								idValue, 
		    								reader.getAttributeValue(null, srcAttr)
		    								,currentTimeContainer));
		    				
		    			}else if(reader.getLocalName()==seqElem) {
		    				if(currentTimeContainer==null) { //first container, the mother seq
		    					currentTimeContainer = new SeqContainer(idValue,null,file);
		    					file.setRootContainer((SeqContainer)currentTimeContainer);
		    				}else{
		    					SeqContainer sc = new SeqContainer(idValue,currentTimeContainer,file);
		    					currentTimeContainer.addChildContainer(sc);
		    					currentTimeContainer = sc;
		    				}
		    			}else if(reader.getLocalName()==metaElem) {
		    				String metaName = reader.getAttributeValue(null, nameAttr);
		    				if(metaName.toLowerCase().equals(timeInThisSmilAttrName)) {
		    					file.setDuration(reader.getAttributeValue(null, contentAttr));
		    				}
		    			}
		    		} else if(reader.isEndElement()) {
		    			if(reader.getLocalName()==parElem || reader.getLocalName()==seqElem) {
		    				currentTimeContainer = currentTimeContainer.getParentContainer();
		    				//if this is the mother seq closing, currentTimeContainer is now null
		    				//but thats ok since we wont encounter any more children.
		    			}
		    		}
				}
			} finally {
				if(reader!=null)reader.close();
				if(inputStream!=null)inputStream.close();
			}
			return true;
		}
	}
		
	static final class MalformedSmilReader {
		public static boolean read(URL smil, SmilFile parent) throws XMLStreamException {
			throw new IllegalStateException("MalformedSmilReader not implemented");	 //$NON-NLS-1$
		}
	}
	
	private static final String parElem = "par"; //$NON-NLS-1$
	private static final String seqElem = "seq"; //$NON-NLS-1$
	private static final String textElem = "text"; //$NON-NLS-1$
	private static final String audioElem = "audio"; //$NON-NLS-1$
	private static final String metaElem = "meta"; //$NON-NLS-1$
	private static final String idAttr = "id"; //$NON-NLS-1$
	private static final String srcAttr = "src"; //$NON-NLS-1$
	private static final String clipBeginAttr = "clip-begin"; //$NON-NLS-1$
	private static final String clipEndAttr = "clip-end"; //$NON-NLS-1$
	private static final String nameAttr = "name"; //$NON-NLS-1$
	private static final String contentAttr = "content"; //$NON-NLS-1$
    private static final String systemRequiredAttr = "system-required";	 //$NON-NLS-1$
    private static final String sidebarOnAttr = "sidebar-on"; //$NON-NLS-1$
    private static final String prodnoteOnAttr = "prodnote-on"; //$NON-NLS-1$
    private static final String footnoteOnAttr = "footnote-on"; //$NON-NLS-1$
    private static final String pagenumOnAttr = "pagenumber-on"; //$NON-NLS-1$
    private static final String timeInThisSmilAttrName = "ncc:timeinthissmil"; //$NON-NLS-1$


}
