package org.daisy.reader.history;

import java.util.EventObject;

public class HistoryEvent extends EventObject {
	
	private HistoryEntry entry;
	private Type type;

	public HistoryEvent(HistoryList source, HistoryEntry entry, Type type) {
		super(source);
		this.entry = entry;
		this.type = type;
	}

	public HistoryEntry getEntry() {
		return entry;
	}
	
	public Type getType() {
		return type;
	}

	public enum Type {
		ADDED, REMOVED;
	}
	
	private static final long serialVersionUID = -7583029634122944991L;

}
