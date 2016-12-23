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
		code.add("ld de,IntVar + " + 3 * (id.charAt(0) - 'A'));
		code.add("call LoadIntVar");
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return id;
	}

	public String getIdentifier() {
		return id;
	}

	public Type getType() {
		return type;
	}
}
