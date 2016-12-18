package in.vvest.compiler;

import java.util.List;

public class Identifier extends Token {

	private Type type;
	private String id;
	
	public Identifier(Type type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public void compile(List<String> code) {
		throw new RuntimeException("Unimplimented");
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return id;
	}
}
