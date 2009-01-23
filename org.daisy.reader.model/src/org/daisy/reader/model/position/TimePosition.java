package org.daisy.reader.model.position;

import org.daisy.reader.model.smil.SmilClock;

/**
 * A time position within the presentation as a whole.
 */

public class TimePosition implements IAutonomousPosition {
	private SmilClock mPosition = null;
	
	public TimePosition(SmilClock position) {
		mPosition = position;
	}
	
	public TimePosition(long millis) {
		mPosition = new SmilClock(millis);
	}
	
	@Override
	public String toString() {
		return mPosition.toString(SmilClock.FULL);
	}

	public SmilClock getClock() {
		return mPosition;
	}
}
