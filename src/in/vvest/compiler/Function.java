package in.vvest.compiler;

import java.util.List;

public class Function extends Token {
	public static final int VARIADIC_ARITY = -1; // A -1 represents a variadic function

	private int arity;
	private String label;

	public Function(String label, int arity) {
		super();
		this.label = label;
		this.arity = arity;
	}

	public void compile(List<String> code) {
		if (children.size() != arity) System.err.println("Compile Error. Too many arguments to " + label);
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
		return new Function(label, arity);
	}
}
