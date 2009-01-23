package org.daisy.reader.model.z2005;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.ISmilLoader;
import org.daisy.reader.model.smil.ParContainer;
import org.daisy.reader.model.smil.SeqContainer;
import org.daisy.reader.model.smil.SmilFile;
import org.daisy.reader.model.smil.TextMediaObject;
import org.daisy.reader.model.smil.TimeContainer;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.ncx.NcxCustomTest;
import org.daisy.reader.util.StAXFactoryProxy;

public class Z2005SmilLoader implements ISmilLoader {

	public boolean load(URL source, SmilFile smilFile) {
		
		try{
			return DefaultZedSmilReader.read(source,smilFile);
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
	
	static final class DefaultZedSmilReader {
		
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
		    				
		    				String begin = reader.getAttributeValue(null, clipBeginAttr);
		    				if(begin==null) begin = reader.getAttributeValue(null, clipBeginAttrHyphened);
		    				String end = reader.getAttributeValue(null, clipEndAttr);
		    				if(end==null) begin = reader.getAttributeValue(null, clipEndAttrHyphened);
		    				
		    				currentTimeContainer.addMediaObject(		    						
		    						new AudioMediaObject(
		    								idValue, 
		    								reader.getAttributeValue(null, srcAttr),
		    								begin,
		    								end,
		    								currentTimeContainer));		    				
		    			
		    			}else if(reader.getLocalName()==parElem) {
		    				ParContainer pc = new ParContainer(idValue, currentTimeContainer, file);
		    				currentTimeContainer.addChildContainer(pc);
		    				currentTimeContainer = pc;
		    				
		    				skippable = reader.getAttributeValue(null, customTestAttr);
		    						    				
		    				if(skippable!=null) {
		    					try{
		    						//need to query the NCX
			    					Ncx navigation = (Ncx)ModelManager.getModel().getNavigation();
			    					NcxCustomTest ct = navigation.getCustomTest(skippable);
			    					if(ct!=null) 		    					
			    						pc.setSemantic(ct.getSemantic());
		    					}catch (Exception e) {
									Activator.getDefault().logError(e.getMessage(), e);
								}
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
		    					String dur = reader.getAttributeValue(null, durAttr);
		    					if(dur!=null) file.setDuration(dur);
		    				}else{
		    					SeqContainer sc = new SeqContainer(idValue,currentTimeContainer,file);
		    					currentTimeContainer.addChildContainer(sc);
		    					currentTimeContainer = sc;
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
	private static final String idAttr = "id"; //$NON-NLS-1$
	private static final String srcAttr = "src"; //$NON-NLS-1$
	private static final String clipBeginAttr = "clipBegin"; //$NON-NLS-1$
	private static final String clipEndAttr = "clipEnd"; //$NON-NLS-1$
	private static final String clipBeginAttrHyphened = "clip-begin"; //$NON-NLS-1$
	private static final String clipEndAttrHyphened = "clip-end"; //$NON-NLS-1$
    private static final String customTestAttr = "customTest";	 //$NON-NLS-1$
    private static final String durAttr = "dur"; //$NON-NLS-1$


}
