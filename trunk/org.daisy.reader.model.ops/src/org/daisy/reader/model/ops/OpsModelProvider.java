package org.daisy.reader.model.ops;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.exception.ModelInstantiationException;
import org.daisy.reader.model.metadata.Metadata;
import org.daisy.reader.model.navigation.INavigation;
import org.daisy.reader.model.provide.IModelProvider;
import org.daisy.reader.ncx.Ncx;
import org.daisy.reader.util.Dtbook;
import org.daisy.reader.util.FileUtils;
import org.daisy.reader.util.MIMEConstants;
import org.daisy.reader.util.TempDir;
import org.daisy.reader.util.XmlUtils;

public class OpsModelProvider implements IModelProvider {

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
			 * components has a catalog impl for XHTML 1.1, but not all).
			 * 
			 * Use xml stream to get optimized speed 
			 */
			//TODO may wanna have a more liberal detection than mime string
			if (item.mItemMediaType.equals(MIMEConstants.MIME_APPLICATION_X_DTBOOK_XML)){					
				try{									
					Dtbook.htmlize(item.mItemURL, f);
				} catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);
					copyFile(item.mItemURL,f);
				}	
			} else if(item.mItemMediaType.equals(MIMEConstants.MIME_APPLICATION_XHTML_XML)){
				try{									
					XmlUtils.stripDocType(item.mItemURL, f);
				} catch (Exception e) {
					Activator.getDefault().logError(e.getMessage(), e);
					copyFile(item.mItemURL,f);
				}				
			}
			else {			
				try{
					copyFile(item.mItemURL,f);
				}catch (IOException e) {
					Activator.getDefault().logError(e.getLocalizedMessage(), e);
				}	
			}
			
			if(isContentDoc(item, packageFile)) {
				extracted.add(new PackageFileItem(item.mItemID,item.mHref,dest.toURL(),item.mItemMediaType));
			}	
		}
		return extracted;
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
