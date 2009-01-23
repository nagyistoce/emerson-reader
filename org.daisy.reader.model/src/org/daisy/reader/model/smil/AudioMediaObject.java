package org.daisy.reader.model.smil;

public class AudioMediaObject extends MediaObject {
	String clipBegin; 
	String clipEnd;
	SmilClock clockBegin;
	SmilClock clockEnd;
			
	/**
	 * Constructor.
	 * @param id The value of the XML audio@id
	 * @param src The value of the XML audio@src
	 * @param clipBegin The value of the XML audio@clip-begin
	 * @param clipEnd The value of the XML audio@clip-end
	 * @param parent The time container in which this audio object appears
	 */
	public AudioMediaObject(String id, String src, String clipBegin, String clipEnd, TimeContainer parent) {
		super(id,src,parent);
		this.clipBegin = clipBegin;
		this.clipEnd = clipEnd;		
	}	
			
	public SmilClock getStartClock() {
		if(clockBegin==null)
			clockBegin = new SmilClock(clipBegin);
		return clockBegin;
	}
	
	public SmilClock getEndClock() {
		if(clockEnd==null)
			clockEnd = new SmilClock(clipEnd);
		return clockEnd;
	}
	
	/**
	 * Get the duration of this clip in milliseconds
	 */
	public long getDuration() {
		return getEndClock().millisecondsValue() 
			- getStartClock().millisecondsValue();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AudioMediaObject "); //$NON-NLS-1$
		sb.append("id=" + this.id + " " ); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("parentSmil=" + getParentContainer().getParentFile().getLocalName()+ " " ); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("src=" + this.src + " " ); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("clockStart=" + this.clipBegin + " " ); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("clockEnd=" + this.clipEnd + " " );		 //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}	
}