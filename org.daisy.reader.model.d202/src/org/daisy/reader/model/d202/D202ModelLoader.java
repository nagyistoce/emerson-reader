package org.daisy.reader.model.d202;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.semantic.Semantic;
import org.daisy.reader.model.smil.ISmilLoader;
import org.daisy.reader.model.smil.SmilSpine;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.ncx.NcxNavList;
import org.daisy.reader.ncx.NcxNavMap;
import org.daisy.reader.ncx.NcxNavPoint;
import org.daisy.reader.ncx.NcxPageTarget;
import org.daisy.reader.util.StAXFactoryProxy;
import org.daisy.reader.util.URIStringParser;

/*
 * Adopt a lazy loading approach as to minimize time elapsed until
 * returning.
 * 
 * The idea is to parse the NCC (build entire NavigationModel) and
 * SMIL file proxies, then return, parse subsequent SMIL files as 
 * they are requested. 
 */
final class D202ModelLoader {
		
	private static Pattern pagePattern;
	
	static D202Model load(URL ncc) throws Exception {

		D202Ncx navigation = new D202Ncx(ncc);
		SmilSpine spine = new SmilSpine();
		Metadata metadata = new Metadata(ncc);
		
		//create an ISmilLoader to pass into to SmilFile objects (lazy parsing)
		ISmilLoader smilLoader = new D202SmilLoader();
		
		/*
		 * Read the ncc, populate navigation and spine objects.
		 * The entire ncc is parsed instantly, but the SmilSpines
		 * SmilFiles are lazy and uses proxied accessors.
		 * 
		 * We do not spend time here to check whether referenced
		 * SMIL files actually exist etc. The default assumption
		 * is that the DTB is in an OK state.
		 */

		try{
			DefaultNccReader.read(ncc,navigation,spine,metadata,smilLoader);
		}catch (XMLStreamException e) {
			Activator.getDefault().logError(e.getMessage(), e);
			throw e;
			//TODO the ncc was probably malformed, try again			
//			navigation = new D202Ncx(ncc);
//			spine = new SmilSpine();
//			TagSoupNccReader.read(ncc,navigation,spine,metadata,smilLoader);
		}	
			
//		//set the initial audio position
//		spine.getAudioClipCursor().set(spine.getFirstAudioMediaObject());
		
		return new D202Model(ncc,navigation,metadata,spine);	

	}
		
	/**
	 * A worker class that builds the Ncx, Metadata and SmilSpine objects using
	 * a StAX-parse of the input NCC.
	 */
	static final class DefaultNccReader {
		
		private enum Type {
			HEADING,
			PAGE;
		}
		
		public static void read(URL nccURL, Ncx ncx, SmilSpine spine, Metadata metadata, ISmilLoader smilLoader) throws Exception {
			//XMLStreamReader2 reader = StAXFactoryProxy.getXMLInputFactory().createXMLStreamReader(nccURL);
			InputStream inputStream = nccURL.openStream();
			XMLStreamReader reader = StAXFactoryProxy.getXMLInputFactory().createXMLStreamReader(inputStream);
	    	try{
	    		CharBuffer chBuf = null;
	    		boolean inAnchor = false;
	    		boolean ignore = false;
	    		
	    		String curLabel = null;
	    		String curTarget = null;
	    		Type curType = null;
	    		int curPlayOrder = 0;	    		
	    		Object curParent = null; //navMap or navPoint
	    		NcxNavPoint lastAddedNavPoint = null; //navPOint that was added last
	    		int lastAddedNavPointDepth = -1; //depth of the navPoint that was added last
	    		int curNavPointDepth = -1; //depth of the navPoint about to be added
	    		NcxNavList pageList = null;
		    	
	    		while(reader.hasNext()) {
		    		reader.next();
		    		if(reader.isStartElement()) {
		    			
		    			if(reader.getLocalName()==anchorElem && !ignore) { 
		    				inAnchor = true;
		    				String smilURI = reader.getAttributeValue(null, hrefAttr);
		    				try{
		    					spine.add(URIStringParser.stripFragment(smilURI),nccURL,smilLoader);
		    				}catch (MalformedURLException e) {		    					
		    					Activator.getDefault().logError(e.getMessage(), e);
		    					ignore = true;
		    					continue;
							}
		    				curTarget = smilURI;	    				
		    			}
		    		
		    			else if(reader.getLocalName() == spanElem
		    					&& getPagePattern().matcher(
		    							reader.getAttributeValue(
		    									null, classAttr)).matches()) {
		    				ignore = false;
		    				curType = Type.PAGE;			
		    			}
		    		
		    			else if(reader.getLocalName().startsWith("h") && //$NON-NLS-1$
		    					reader.getLocalName().length()==2) {		    				
		    				ignore = false;
		    				curType = Type.HEADING;		    				
		    				try{
		    					//identify curParent, to whose children list the new NavPoint shall be added
		    					curNavPointDepth = Integer.parseInt(reader.getLocalName().substring(1));		    					
		    					if(lastAddedNavPoint==null) {
		    						curParent = ncx.getNavMap();
		    					}else{
		    						if(curNavPointDepth==lastAddedNavPointDepth) {
		    							curParent = lastAddedNavPoint.getParent();
		    						}else if (curNavPointDepth>lastAddedNavPointDepth) {
		    							curParent = lastAddedNavPoint;
		    						}else{
		    							//step back upwards
		    							int diff = lastAddedNavPointDepth - curNavPointDepth;
		    							curParent = lastAddedNavPoint.getParent();
		    							for (int i = 0; i < diff; i++) {
											curParent = ((NcxNavPoint)curParent).getParent();
										}
		    						}		    						
		    					}	
		    					lastAddedNavPointDepth = curNavPointDepth;
		    				}catch (Exception e) {		    					
		    					Activator.getDefault().logError(e.getLocalizedMessage(), e);
		    					curParent = ncx.getNavMap();
							}		    				
		    			}
		    			
		    			else if(reader.getLocalName() == metaElem) {
		    				ignore = false;
		    				String name = reader.getAttributeValue(
									null, nameAttr);
		    				if(name!=null) {
		    					name = name.toLowerCase();
		    					if(name.equals(dcIdentifier)) {
		    						metadata.put(Metadata.UUID, reader.getAttributeValue(null,contentAttr));		    						
		    					}else if(name.equals(dcTitle)) {
		    						metadata.put(Metadata.TITLE, reader.getAttributeValue(null,contentAttr));		    						
		    					}else if(name.equals(dcCreator)) {
		    						metadata.put(Metadata.AUTHOR, reader.getAttributeValue(null,contentAttr));
		    					}else if(name.equals(nccTotalTime)) {
		    						spine.setDuration(reader.getAttributeValue(null,contentAttr));
		    					}
		    				}
		    			}
		    			
		    			else {
		    				ignore = true;
		    			}
		    		}else if(reader.isCharacters() && inAnchor) {
		    			char[] chrs = new char[reader.getTextLength()];	    			
		    			reader.getTextCharacters(0,chrs,0,reader.getTextLength());	    			
		    			if(chBuf==null) {
		    				//TODO only create a charbuffer if there are 
		    				//more than one Characters call
		    				chBuf = CharBuffer.wrap(chrs);
		    			}else{
		    				char[] buf = chBuf.array();
		    				chBuf = CharBuffer.allocate(buf.length+chrs.length);
		    				chBuf.put(buf).put(chrs);
		    			}	    			
		    		}else if(reader.isEndElement() && reader.getLocalName()==anchorElem && !ignore) {
		    			curLabel = String.copyValueOf(chBuf.array());
		    			
		    			//create and commit the NcxItem
		    			if(!ignore) {
		    				curPlayOrder++;
		    				if(curType== Type.HEADING) {
		    					NcxNavPoint navPoint = new NcxNavPoint(curParent,curLabel,curTarget,curPlayOrder); 
		    					lastAddedNavPoint = navPoint;
		    					if(curParent instanceof NcxNavMap) {
		    						((NcxNavMap)curParent).getChildren().add(navPoint);
		    					}else{
		    						((NcxNavPoint)curParent).getChildren().add(navPoint);
		    					}
		    				}else if(curType== Type.PAGE) {		 
		    					if(pageList==null)
		    						pageList = ncx.addNavList(Semantic.PAGE_NUMBER);			    					
		    					NcxPageTarget pageTarget = new NcxPageTarget(pageList,curLabel,curTarget,curPlayOrder);
		    					pageList.add(pageTarget);		    					
		    				}
		    			}
		    			curTarget = null;
		    			curLabel = null;
		    			curType = null;
		    			curParent = null;		    					    			
		    			chBuf = null;
		    			inAnchor = false;
		    		}
		    	} // while reader.hasNext()
	    	} catch (XMLStreamException e) {
	    		throw e;
	    	} finally {
	    		if(reader!=null)reader.close();
	    		if(inputStream!=null)inputStream.close();
	    	}
			
		}
		
	}
	
	/**
	 * A worker object that builds the D202Navigation and SmilSpine objects using
	 * a Tagsoup+chardet setup to parse the input NCC.
	 */
	static final class TagSoupNccReader {

		public static void read(URL ncc, Ncx navigation, SmilSpine spine, Metadata metadata, ISmilLoader smilLoader) throws Exception {
			//TODO TagSoupNccReader
			throw new IllegalStateException("TagSoupNccReader not implemented"); //$NON-NLS-1$
			
		}
		
	}
		
	private static Pattern getPagePattern() {
		if(pagePattern==null) {
			pagePattern = Pattern.compile("page-normal|page-front|page-special"); //$NON-NLS-1$
		}
		return pagePattern;
	}
	
	//private static final char hash = '#';
	private static final String anchorElem = "a"; //$NON-NLS-1$
	private static final String spanElem = "span"; //$NON-NLS-1$
	private static final String metaElem = "meta"; //$NON-NLS-1$
	//private static final String idAttr = "id"; //$NON-NLS-1$
	private static final String hrefAttr = "href"; //$NON-NLS-1$
	private static final String nameAttr = "name"; //$NON-NLS-1$
	private static final String contentAttr = "content"; //$NON-NLS-1$
	private static final String classAttr = "class"; //$NON-NLS-1$
	private static final String dcIdentifier = "dc:identifier"; //$NON-NLS-1$
	private static final String dcCreator= "dc:creator"; //$NON-NLS-1$
	private static final String dcTitle = "dc:title"; //$NON-NLS-1$
	private static final String nccTotalTime = "ncc:totaltime"; //$NON-NLS-1$
}
