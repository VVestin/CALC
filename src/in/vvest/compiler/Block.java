package in.vvest.compiler;

import java.util.List;

public class Block extends Token {

	public void compile(List<String> code) {
		for (Token child : children) {
			child.compile(code);
		}
	}

	public boolean isCompileable() {
		return true;
	}

}
