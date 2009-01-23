package org.daisy.reader.model.ops;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

public class PackageFileLoader {

	public static PackageFile getPackage(URI uri, String mime) throws XMLStreamException, IOException, URISyntaxException {
		
		URL packageURL = null;
		if(mime.equals(EpubFile.MIMETYPE)) {
			EpubFile epub = new EpubFile(uri);
			packageURL = epub.getPackageFileURL();
		}else{
			packageURL = uri.toURL();	
		}
		
		return new PackageFile(packageURL);
	}
	
}
