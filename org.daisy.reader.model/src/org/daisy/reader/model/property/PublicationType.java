package org.daisy.reader.model.property;

import org.daisy.reader.model.Model;

public class PublicationType {
	private final String niceName;
	private final String techName;
	private final String implementor;
	
	public PublicationType(String niceName, String techName,
			Class<? extends Model> implementor) {
		this.niceName = niceName;
		this.techName = techName;
		this.implementor = implementor.getName();
	}
	
	public PublicationType(String niceName, String techName,
			String implementor) {
		this.niceName = niceName;
		this.techName = techName;
		this.implementor = implementor;
	}

	/**
	 * A String suitable for end users.
	 */
	public String getNiceName() {
		return niceName;
	}

	/**
	 * A String suitable for geeks.
	 */
	public String getTechName() {
		return techName;
	}

	/**
	 * Get the name of a Model subclass that implements this type of publication
	 * @return
	 */
	public String getImplementor() {
		return implementor;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) return true;
		if(!(obj instanceof PublicationType)) return false;
		PublicationType in = (PublicationType)obj;
		if(!this.niceName.equals(in.niceName)) return false;
		if(!this.techName.equals(in.techName)) return false;
		if(!this.implementor.equals(in.implementor)) return false;
		return true;
	}
	
	@Override
	public String toString() {		
		return techName;
	}
	
}
