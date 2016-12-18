package in.vvest.compiler;

import java.util.List;

public class Function extends Token {

	private String label;

	public Function(String label) {
		super();
		this.label = label;
	}

	public void compile(List<String> code) {
		for (Token t : children) {
			t.compile(code);
		}
		code.add("call " + label);
		// TODO add support for variadic functions (Disp, Min, Max, etc.)
	}

	public String getValue() {
		return label;
	}

	public boolean isCompileable() {
		return true;
	}

	public Token copy() {
		return new Function(label);
	}
}
