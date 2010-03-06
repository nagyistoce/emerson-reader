package org.daisy.emerson.ui.browser.delegate;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.daisy.emerson.ui.browser.BrowserView;
import org.daisy.emerson.ui.browser.IBrowserTargetChangeListener;
import org.daisy.reader.model.ModelManager;
import org.daisy.reader.model.audio.Activator;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.position.SmilAudioPosition;
import org.daisy.reader.model.position.URIPosition;
import org.daisy.reader.model.smil.AudioMediaObject;
import org.daisy.reader.model.smil.MediaObject;
import org.daisy.reader.model.smil.SmilFile;
import org.daisy.reader.model.smil.SmilSpine;
import org.daisy.reader.model.smil.TextMediaObject;
import org.daisy.reader.util.URIStringParser;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

/**
 * A delegate handling link activations.
 * @author Markus Gylling
 */
public class BrowserLinkActivationDelegate implements IBrowserBehaviorDelegate, IBrowserTargetChangeListener, MouseListener {
	
	private BrowserView browserView;
	private boolean isJavascriptLoaded = false;
	private BrowserFunction linkCallbackFunction = null;
	private boolean isDisposed = false;
	
	public BrowserLinkActivationDelegate() {		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#initialize(org.daisy.emerson.ui.browser.BrowserView)
	 */
	public IBrowserBehaviorDelegate initialize(BrowserView browserView) {
		this.browserView = browserView;		
		browserView.addTargetChangeListener(this);
		browserView.browser.addMouseListener(this);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#dispose()
	 */
	public void dispose() {
		if(browserView !=null) {
			browserView.removeTargetChangeListener(this);
			if(browserView.browser!=null && !browserView.browser.isDisposed()) {
				browserView.browser.removeMouseListener(this);
			}
		}	
		
		if(linkCallbackFunction!=null && !linkCallbackFunction.isDisposed()) {
			linkCallbackFunction.dispose();
			isJavascriptLoaded = false;
		}
		
		isDisposed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.IBrowserTargetChangeListener#preTargetChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void preTargetChange(String newBaseURL, String prevBaseURL,
			String newFragment, String prevFragment) {
		//noop								
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.IBrowserTargetChangeListener#preTargetChange(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void postTargetChange(String newBaseURL, String prevBaseURL,
			String newFragment, String prevFragment) {
		
		if(newBaseURL!=null && !newBaseURL.equals(prevBaseURL)) {
			//if new document, set javascriptInserted to false
			if(!newBaseURL.equals(prevBaseURL)) {
				isJavascriptLoaded = false;
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
		//noop		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
		/*
		 * Because the load progress events are tricky, we do the javascript
		 * insertion for click handling at first click instead of at
		 * document load. Note that, at least on safari, load progress never completes
		 * if the document links to nonexisting resources, and that mouse events
		 * aren't propagated until progress is complete...
		 * 
		 * Note also: we only use the listener here to load the JS; the JS itself
		 * registers the actual activation/click handlers.
		 */
		
		if(!isJavascriptLoaded) {
			synchronized (e) {
				loadJavaScript();
			}	
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {

	}

	private void loadJavaScript() {
		
		if(browserView==null || browserView.browser==null || browserView.browser.isDisposed()) return;
		
		browserView.execute("document.body.setAttribute('onclick','return checkTraversalCancel(event)');");
		browserView.execute("document.body.setAttribute('onmouseup','elementInfo(event)');");
		browserView.execute(addScriptElement);				
		
		if(linkCallbackFunction!=null) {
			linkCallbackFunction.dispose();
		}
		linkCallbackFunction = new LinkActivationCallbackFunction (browserView.browser, "emersonLinkHandler");
		
		isJavascriptLoaded = true;
	}
			
	class LinkActivationCallbackFunction extends BrowserFunction {
		
		LinkActivationCallbackFunction (Browser browser, String name) {
			super (browser, name);
		}
		
		public Object function (Object[] arguments) {
			
			//{nodeName, id, parentid, link}, where the string "null" means null;			
			
			String nodeName = (arguments[0]!=null) ? (String) arguments[0] : null;
			if("null".equals(nodeName)) nodeName = null;
			String nodeID = (arguments[1]!=null) ? (String) arguments[1] : null;
			if("null".equals(nodeID)) nodeID = null;
			String parentNodeID = (arguments[2]!=null) ? (String) arguments[2] : null;
			if("null".equals(parentNodeID)) parentNodeID = null;
			String linkURL = (arguments[3]!=null) ? (String) arguments[3] : null;
			if("null".equals(linkURL)) linkURL = null;
			
//			System.err.println("emerson-link-handler#function(): nodeName=" 
//					+ nodeName + " nodeID=" + nodeID + " parentNodeID=" 
//					+ parentNodeID + " linkURL=" + linkURL);
					
			IPosition position = null;
			
			String cur = browserView.browser.getUrl(); //TODO get this from local listener
			
			//if link is a smil ref, then find that SMIL fragment and activate			
			if(linkURL != null && linkURL.toLowerCase().contains(".smil")) {
				try{					
					if(cur!=null) {
						String curu = URIStringParser.stripFragment(cur);
						URL referer = new URI(curu).toURL();
						position = new URIPosition(linkURL,referer);
					}				 				
				}catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);
				}
			}
			
			//if link == null but current or parent has an ID, look for a SMIL text reference
			// to those IDs in the smil spine. Note that not all models have SMIL spines.
			if(position == null && (nodeID !=null || parentNodeID != null) && cur!=null) {		
				//TODO this could be optimized, for example by searching forward from current
				//model position
				Object o = ModelManager.getModel().getSpine();
				if(o instanceof SmilSpine) {					
					SmilSpine spine = (SmilSpine)o;
					String filename = URIStringParser.getFileLocalName(cur);
					boolean found = false;
					for(SmilFile smf : spine) {
						if(found) break;
						try{
							List<MediaObject> texts = smf.getRootContainer().getMediaChildren(TextMediaObject.class);
							for(MediaObject mo : texts) {
								if(found) break;
								TextMediaObject text = (TextMediaObject) mo;
								if(text.getSrc().contains(filename)) {
									String fragment = URIStringParser.getFragment(text.getSrc());
									if(fragment.equals(nodeID) || fragment.equals(parentNodeID)) {
										AudioMediaObject amo = (AudioMediaObject)
											text.getParentContainer().getFirst(AudioMediaObject.class);
										if(amo!=null) {
											position = new SmilAudioPosition(amo);
											found = true;
										}											
									}
								}
							}	
						}catch (Exception e) {
							Activator.getDefault().logError(e.getMessage(), e);
						}
					}
				}
			}
			
			if(position!=null) {
				ModelManager.getModel().setPosition(position);
			}
			return null;
		}
	}

	
	private static final String scriptFunctions = 
		"function elementInfo(e) {"+
        "	var targ = getElement(e);"+                 
        "	var link = targ.getAttribute(\"smilref\");"+
        "	if(!link) link = targ.href;"+   
        "	if(!link) link = \"null\";"+
        "	var id = targ.id;"+
        "	if(!id) id = \"null\";"+
        "	var parentid =  targ.parentNode.id;"+
        "	if(!parentid) parentid = \"null\";"+                                                
        "	emersonLinkHandler(targ.nodeName, id, parentid, link)"+
    	"}"+            
    	"function checkTraversalCancel(e) {" +
        "	var targ = getElement(e);"+ 
        "	var link = targ.getAttribute(\"smilref\");"+
        "	if(!link) link = targ.getAttribute(\"href\");"+
        "	if(!link) return true;"+
        "	if(link.toLowerCase().indexOf(\".smil\") > 0) return false;"+
    	"}"+            
    	"function getElement(e) {" +
        " 	var targ;"+ 
        "	if (!e) var e = window.event;"+
        "	if (e.target) targ = e.target;"+
        "	else if (e.srcElement) targ = e.srcElement;"+
        "	if (targ.nodeType == 3) targ = targ.parentNode;"+
        "	return targ;" +
    	"}";
	
	private static final String addScriptElement = 
		"var script = document.createElement('script');\n" +
		"script.setAttribute('type','text/javascript');\n" +
		"var scriptContent = document.createTextNode('" + scriptFunctions + "');\n" +
		"script.appendChild(scriptContent);\n" + 
		"document.documentElement.getElementsByTagName('head')[0].appendChild(script);\n";

	/*
	 * (non-Javadoc)
	 * @see org.daisy.emerson.ui.browser.delegate.IBrowserBehaviorDelegate#isDisposed()
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

}
