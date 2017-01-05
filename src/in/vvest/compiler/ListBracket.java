package in.vvest.compiler;

import java.util.List;

public class ListBracket extends Token {
	private boolean open;

	public ListBracket(boolean open) {
		super();
		this.open = open;
	}

	public void compile(List<String> code) { }

	public boolean isCompileable() {
		return false;
	}

	public String getValue() {
		return open ? "[" : "]";
	}

	public boolean isOpen() {
		return open;
	}
}
