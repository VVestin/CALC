package in.vvest.compiler;

import java.util.List;

public class Parenthesis extends Token {
	private boolean open;

	public Parenthesis(boolean open) {
		this.open = open;
	}

	public void compile(List<String> code) {}

	public boolean isCompileable() {
		return false;
	}

	public boolean isOpen() {
		return open;
	}
}
