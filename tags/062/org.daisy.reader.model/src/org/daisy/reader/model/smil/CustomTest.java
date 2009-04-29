package org.daisy.reader.model.smil;

import org.daisy.reader.Activator;
import org.daisy.reader.model.semantic.Semantic;

public class CustomTest {

	private Semantic semantic;
	private String bookStruct;
	private boolean defaultState;
	private String id;
	private String override;
		
	public CustomTest(String bookStruct, boolean defaultState, 
			String id, String override) {
				
		this.bookStruct = bookStruct;
		this.defaultState = defaultState;
		this.id = id;
		this.override = override;
		try {
			this.semantic = Semantic.valueOf(bookStruct);
		}catch (Exception e) {
			Activator.getDefault().logError(e.getMessage(), e);
			this.semantic = Semantic.PHRASE;
		}					
	}
	
	public Semantic getSemantic() {
		return semantic;
	}
	
	public String getBookStruct() {
		return bookStruct;
	}
	
	public boolean getDefaultState() {
		return defaultState;
	}
	
	public String getId() {
		return id;
	}
	
	public String getOverride() {
		return override;
	}

}
