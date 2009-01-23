package org.daisy.reader.history;

import java.net.URL;
import java.util.Date;

import org.daisy.reader.model.Model;
import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.position.IPosition;
import org.daisy.reader.model.property.IPropertyConstants;
import org.daisy.reader.model.property.PublicationType;
import org.daisy.reader.util.User;

public class HistoryEntry {
	String publicationUID;
	String publicationTitle;
	PublicationType publicationType;
	String publicationAuthor;
	String userUID;
	URL lastManifestLocation;
	IPosition position;
	long entryLastAccessDate;
	
	public HistoryEntry(
			IAutonomousPosition position,
				String publicationUID,
					URL lastManifestLocation,
						String userUID,
							String publicationTitle,
								String publicationAuthor,
									PublicationType publicationType) {		
		
		this(position,publicationUID,lastManifestLocation,userUID,null,publicationTitle,publicationAuthor,publicationType);
	}
	
	public HistoryEntry(
			IAutonomousPosition position,
				String publicationUID,
					URL lastManifestLocation,
						String userUID,
							Long entryCreationDate,
								String publicationTitle,
									String publicationAuthor,
									PublicationType publicationType) {
		
		
		if( /*position==null||*/ publicationUID==null 
				|| lastManifestLocation ==null||userUID==null||publicationType==null) {
			throw new NullPointerException();
		}
		
		this.lastManifestLocation = lastManifestLocation;
		this.position = position;
		this.publicationUID = publicationUID;
		this.publicationTitle = publicationTitle;
		this.publicationAuthor = publicationAuthor;
		this.publicationType = publicationType;
		this.userUID = userUID;
		if(entryCreationDate == null) {
			this.entryLastAccessDate = new Date().getTime();
		}else{
			this.entryLastAccessDate = entryCreationDate;
		}		
	}
	
	public String getPublicationUID() {
		return publicationUID;
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public String getPublicationAuthor() {
		return publicationAuthor;
	}
	
	public PublicationType getPublicationType() {
		return publicationType;
	}
	
	public String getUserUID() {
		return userUID;
	}

	/**
	 * Get the last recorded locus of the manifest of the publication.
	 */
	public URL getLastManifestLocation() {
		return lastManifestLocation;
	}

	/**
	 * Get the last recorded user position within the publication.
	 */
	public IPosition getLastPosition() {
		return position;
	}

	/**
	 * Get the last time this entry was loaded by the user.
	 */
	public long getLastAccessDate() {
		return entryLastAccessDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) return true;
		try{
			if(obj instanceof HistoryEntry) {
				HistoryEntry in = (HistoryEntry)obj;
				if(in.publicationUID.equals(this.publicationUID)
						&& in.userUID.equals(this.userUID) 
						&& in.publicationType.equals(this.publicationType)) {					
					return true;
				}
			}if(obj instanceof Model) {				
				Model model = (Model)obj;
				if(!model.getProperty(IPropertyConstants.PUBLICATION_UUID)
						.equals(this.publicationUID)) 
					return false;
				if(!model.getProperty(IPropertyConstants.PUBLICATION_URL)
						.equals(this.lastManifestLocation)) 
					return false;	
				if(!User.getID().equals(this.userUID)) 
					return false;
				if(!model.getProperty(IPropertyConstants.PUBLICATION_TYPE)
						.equals(this.publicationType)) 
					return false;				
				return true;
			}
		}catch (Exception e) {
			
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("publicationUID: " + publicationUID); //$NON-NLS-1$
		sb.append("\nuserUID: " + userUID); //$NON-NLS-1$
		sb.append("\ntype: " + publicationType); //$NON-NLS-1$
		sb.append("\nlastManifestLocation: " + lastManifestLocation.toString()); //$NON-NLS-1$
		sb.append("\nposition: " + position.toString()); //$NON-NLS-1$
		sb.append("\nentryCreationDate: " + new Date(entryLastAccessDate).toString()); //$NON-NLS-1$
		return sb.toString();
	}
	

	
}
