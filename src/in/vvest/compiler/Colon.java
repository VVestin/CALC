package in.vvest.compiler;

import java.util.List;

public class Colon extends Token {
	public void compile(List<String> code) {}

	public Colon() {
		super();
	}

	public boolean isCompileable() {
		return false;
	}

	public String toString() {
		return "[Colon]";
	}
}
