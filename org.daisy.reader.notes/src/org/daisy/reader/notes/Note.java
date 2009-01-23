package org.daisy.reader.notes;

import org.daisy.reader.model.position.IAutonomousPosition;
import org.daisy.reader.model.property.PublicationType;

public class Note {
	final IAutonomousPosition location;	
	final String publicationUID;
	final PublicationType publicationType;
	final String context;
	final String note;
	final String userID;
	final long date;
	final int sequence;
	
	public Note(String context, long date, IAutonomousPosition location, String note,
			String publicationUID, String userID, int sequence
			, PublicationType publicationType) {
		
		if(publicationUID==null || userID==null) 
			throw new IllegalArgumentException();
				
		this.context = context;	
		this.date = date;
		this.location = location;
		this.note = note;
		this.publicationUID = publicationUID;
		this.userID = userID;		
		this.sequence = sequence;
		this.publicationType = publicationType;
	}
	
	public IAutonomousPosition getLocation() {		
		return location;
	}
	
	public String getPublicationUID() {
		return publicationUID;
	}
	
	/**
	 * May be null or the empty string.
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * May be null or the empty string.
	 */
	public String getContent() {
		return note;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public long getTimeStamp() {
		return date;
	}
	
	public int getSequence() {
		return sequence;
	}	
	
	public PublicationType getPublicationType(){
		return publicationType;
	}
}
