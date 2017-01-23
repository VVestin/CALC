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
		code.add("ld de," + getAddress());
		if (type == Type.INTEGER) {
			code.add("call LoadIntVar");
		} else if (type == Type.STRING) {
			code.add("call LoadStrVar");
		} else if (type == Type.LIST) {
			code.add("call LoadListVar");
		}
	}

	public String getAddress() {
		if (type == Type.INTEGER) {
			return "IntVar+" + 4 * (id.charAt(0) - 'A');
		} else if (type == Type.STRING) {
			return "StrVar+" + 2 * (id.charAt(3) - '0');
		} else if (type == Type.LIST) {
			return "listVar+" + 2 * (id.charAt(1) - '0');
		}
		return "Compile Error. Tried to get address of nonaddressable identifier";
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
