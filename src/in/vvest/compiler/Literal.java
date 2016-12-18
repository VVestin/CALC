package in.vvest.compiler;

import java.util.List;

public class Literal extends Token {

	private Type type;
	private String value;
	
	public Literal(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public void compile(List<String> code) {
		throw new RuntimeException("Unimplimented");
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return value;
	}
}
