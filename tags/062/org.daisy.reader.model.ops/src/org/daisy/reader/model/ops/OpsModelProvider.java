package org.daisy.reader.model.ops;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.exception.ModelInstantiationException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.provide.IModelProvider;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.util.FileUtils;
import org.daisy.reader.util.MIMEConstants;
import org.daisy.reader.util.StreamTransformer;
import org.daisy.reader.util.TempDir;

public class OpsModelProvider implements IModelProvider {

	private Map<String,Object> xhtmlConfig;
	private Map<String,Object> dtbookConfig;
	
	public Model create(URL content) throws ModelInstantiationException {
		
		try {
			//get the package file inside the epub
			PackageFile pkg = PackageFileLoader.getPackage(
					content.toURI(), EpubFile.MIMETYPE);			
							
			//create the metadata object
			Metadata metadata = getMetadata(pkg);
			
			//extract the contents to a temp dir
			File tempdir = TempDir.create(metadata.get(Metadata.UUID));	
			
			List<PackageFileItem> spine = extract(pkg,tempdir);

			//create the ncx object, using the tempdir location
			URI ncxURI = tempdir.toURI().resolve(pkg.mNCX.mHref);
			INavigation ncx = getNcx(ncxURI.toURL());
			
			//create the model using the extracted files			
			return new OpsModel(content, spine, ncx, metadata, tempdir);
			
		} catch (IOException e) {
			throw new ModelInstantiationException(e.getLocalizedMessage(),e);
		} catch (URISyntaxException e) {
			throw new ModelInstantiationException(e.getLocalizedMessage(),e);		
		} catch (XMLStreamException e) {
			throw new ModelInstantiationException(e.getLocalizedMessage(),e);
		}		
	}

	private List<PackageFileItem> extract(PackageFile packageFile, File tempdir) throws IOException, URISyntaxException, XMLStreamException {		
				
		List<PackageFileItem> extracted = new LinkedList<PackageFileItem>();		
		URI tempBaseURI = new File(tempdir,"dummy.baz").toURI(); //$NON-NLS-1$
				
		for(PackageFileItem item :packageFile.mManifest) {			
																	
			URI dest = tempBaseURI.resolve(item.mHref);			
			File f = new File(dest);
			f.getParentFile().mkdirs();
			f.createNewFile();
			f.deleteOnExit();
			
			/*			 
			 * We mod the content docs to resolve DTD entities and remove doctype, 
			 * else offline browser load wont work. (Some browser
			 * components has a catalog impl for XHTML 1.1, but not all. No browser
			 * impl has a catalog impl for DTBook).
			 * 
			 * Use xml stream to get optimized speed.
			 */
			
			if(isContentDoc(item, packageFile)) {				
				try{					
					StreamTransformer.transform(item.mItemURL, f, getTransformConfig(item));					
				} catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);
					try{
						copyFile(item.mItemURL,f);
					}catch (IOException ioe) {
						e.printStackTrace();
						Activator.getDefault().logError(ioe.getLocalizedMessage(), ioe);
						continue;
					}
				}
				
				extracted.add(new PackageFileItem(item.mItemID,item.mHref,f.toURI().toURL(),item.mItemMediaType));
			}else{
				try{
					copyFile(item.mItemURL,f);
				}catch (IOException e) {
					Activator.getDefault().logError(e.getLocalizedMessage(), e);
				}
			}			
		}
		return extracted;
	}

	private Map<String,Object> getTransformConfig(PackageFileItem item) {
		if (item.mItemMediaType.equals(MIMEConstants.MIME_APPLICATION_X_DTBOOK_XML)){
			if(dtbookConfig==null) {
				dtbookConfig = new HashMap<String,Object>();
				dtbookConfig.put(StreamTransformer.KEY_DTD, "");
				dtbookConfig.put(StreamTransformer.KEY_HTTP_EQUIV, Boolean.TRUE);
				//dtbookConfig.put(StreamTransformer.KEY_FORCE_HTML_EXTENSION, Boolean.TRUE);
								
				Map<String,String> nsMap = new HashMap<String,String>();
				nsMap.put(StreamTransformer.NAMESPACE_DTBOOK, StreamTransformer.NAMESPACE_XHTML);
				dtbookConfig.put(StreamTransformer.KEY_NAMESPACE_MAP, nsMap);
				
				Map<String,String> elemMap = new HashMap<String,String>();
				elemMap.put(StreamTransformer.ELEMENT_DTBOOK,StreamTransformer.ELEMENT_HTML);
				elemMap.put(StreamTransformer.ELEMENT_BOOK,StreamTransformer.ELEMENT_BODY);
				dtbookConfig.put(StreamTransformer.KEY_ELEMENT_MAP, elemMap);
			}	
			return dtbookConfig;
		}
		
		if(xhtmlConfig==null) {
			xhtmlConfig = new HashMap<String,Object>();
			xhtmlConfig.put(StreamTransformer.KEY_DTD, "");
			xhtmlConfig.put(StreamTransformer.KEY_HTTP_EQUIV, Boolean.TRUE);
			//xhtmlConfig.put(StreamTransformer.KEY_FORCE_HTML_EXTENSION, Boolean.TRUE);
		}
		return xhtmlConfig;
		
	}
	
	private void copyFile(URL source, File dest) throws IOException {				
		InputStream in = null;		
		try {
			in = source.openStream();
			FileUtils.writeInputStreamToFile(dest,in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	private boolean isContentDoc(PackageFileItem item, PackageFile packageFile) {
		if(packageFile.mSpine.contains(item)) {
			return true;
		}		
		return false;
	}
			
	private INavigation getNcx(URL url) {
		Ncx ncx = new Ncx();
		ncx.load(url);
		return ncx;
	}

	private Metadata getMetadata(PackageFile pkg) {
		Metadata md = new Metadata(pkg.mURL); 
		md.put(Metadata.AUTHOR, pkg.mCreator);
		md.put(Metadata.TITLE, pkg.mTitle);
		md.put(Metadata.UUID, pkg.mUID);
		md.put(Metadata.PUBLISHER, pkg.mPublisher);
		return md;
	}

}
