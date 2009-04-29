package org.daisy.reader.model.ops;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class PackageFileItem {
	/** The id of this item in the package file */
	public String mItemID;
	/** The resolved path to this item in the package file */
	public URL mItemURL;
	/** The stated media type of this item in the package file */
	public String mItemMediaType;
	/** The original href value of the item in the package file */
	public String mHref;
	
	private StringBuilder mStringBuilder = new StringBuilder();
	
	public PackageFileItem(String id, String href, char[] relative, String mediaType) throws URISyntaxException, MalformedURLException {
		mItemID = id;
		mItemMediaType = mediaType;
		mHref = href;
		mItemURL = resolve(href,relative);
		
	}
	
	public PackageFileItem(String id, String href, URL url, String mediaType) throws URISyntaxException, MalformedURLException {
		mItemID = id;
		mHref = href;
		mItemMediaType = mediaType;
		mItemURL = url;
	}
	
	private URL resolve(String href, char[] relative) throws URISyntaxException, MalformedURLException {		
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(relative);
		char[] chars = href.toCharArray();						
		boolean nameChar = false;						
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];				
			if(ch!='.'&& ch!='/') {
				nameChar = true;
			}
			if(nameChar)
				mStringBuilder.append(ch);
		}
		return new URL(mStringBuilder.toString());									 
	}
			
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: ").append(mItemID==null?"null":mItemID.toString()).append("\n");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		sb.append("url: ").append(mItemURL==null?"null":mItemURL.toString()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sb.append("type: ").append(mItemMediaType==null?"null":mItemMediaType.toString()).append("\n");			  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		return sb.toString();			
	}

	public String getFileName() {
		String url = mItemURL.toString();
		return url.substring(url.lastIndexOf('/'));			
	}

}
