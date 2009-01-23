package org.daisy.reader.model.ops;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.daisy.reader.util.StAXFactoryProxy;

class EpubFile {
	public static final String MIMETYPE="application/epub+zip"; //$NON-NLS-1$
	private static final String FULL_PATH = "full-path"; //$NON-NLS-1$
	private static final String MEDIA_TYPE = "media-type"; //$NON-NLS-1$
	private static final String ROOTFILE = "rootfile"; //$NON-NLS-1$
	URI mEpubURI = null;
	
	EpubFile(URI source) throws IOException {
		mEpubURI = source;				
	}

	URL getPackageFileURL() throws IOException, XMLStreamException {
		String name = getPackagePath();
		ZipFile epub = null;
		URL packageURL = null;
		try{			
			epub = new ZipFile(new File(mEpubURI));
			ZipEntry pkg = epub.getEntry(name);			
			packageURL = buildURL(pkg);									
		} finally{
			epub.close();
		}
		return packageURL;
	}
		
	private URL buildURL(ZipEntry pkg) throws MalformedURLException {
		StringBuilder sb = new StringBuilder();
		sb.append("jar:file:");  //$NON-NLS-1$
		sb.append(mEpubURI.getPath()).append("!/");  //$NON-NLS-1$
		sb.append(pkg.getName());			
		return new URL(sb.toString());
	}

	/**
	 * Get the package file path from container.xml
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	private String getPackagePath() throws IOException, XMLStreamException {
		String path = null;
		ZipFile epub = null;
		try{			
			epub = new ZipFile(new File(mEpubURI));
			ZipEntry container = epub.getEntry("META-INF/container.xml");  //$NON-NLS-1$
			if(container==null) {
				throw new IOException(mEpubURI.getPath() 
						+ " does not contain a META-INF/container.xml entry"); //$NON-NLS-1$
			}
			InputStream is = epub.getInputStream(container);
			path = getPathFromContainer(is);
			is.close();	       	       			
		}finally{			
			if(epub!=null)epub.close();
		}	
		return path;
	}

	/**
	 * Parse container.xml 
	 * @throws XMLStreamException 
	 */
	private String getPathFromContainer(InputStream stream) throws XMLStreamException {
		
		XMLInputFactory xif = StAXFactoryProxy.getXMLInputFactory();
		XMLStreamReader reader = null;
		String path = null;
		//<rootfile full-path="OEBPS/package.opf" media-type="application/oebps-package+xml" />
		try{								
			reader = xif.createXMLStreamReader(stream);
			while(reader.hasNext()) {
				reader.next();
				if(reader.isStartElement()) {
					if(reader.getLocalName().equals(ROOTFILE)) {
						String type = reader.getAttributeValue(null, MEDIA_TYPE);
						if(type!=null && type.equals(PackageFile.MIMETYPE)) {
							path = reader.getAttributeValue(null, FULL_PATH);
						}
					}
				}
			}			
		}finally{
			if(reader!=null)reader.close();			
		}
		return path;
	}
	
}
