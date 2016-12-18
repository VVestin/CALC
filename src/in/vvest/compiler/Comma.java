package in.vvest.compiler;

import java.util.List;

public class Comma extends Token {

	public Comma() {
		super();
	}

	public void compile(List<String> code) {}
	
	public boolean isCompileable() {
		return false;
	}
}
