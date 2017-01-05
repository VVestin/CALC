package in.vvest.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

public abstract class Token {
	protected List<Token> children;
	
	public Token() {
		children = new LinkedList<Token>();
	}

	public abstract void compile(List<String> code);
	public abstract boolean isCompileable();

	public Type getType() {
		return Type.VOID;
	}

	public String getValue() {
		return "";
	}

	public List<Token> getChildren() {
		return children;
	}
	
	public void print() {
		print("", false);
	}

	public Token copy() {
		return this;
	}

	public void reverseChildren() {
		Collections.reverse(children);
		for (int i = 0; i < children.size(); i++)
			children.get(i).reverseChildren();
	}

	private void print(String prefix, boolean isTail) {
		System.out.println(prefix + "+--" + this);
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(prefix + (isTail ? "   " : "|  "), false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1).print(prefix + (isTail ?"   " : "|  "), true);
		}
	}

	public void addData(List<String> code) { 
		for (Token child : children) {
			child.addData(code);
		}
	}

	public String toString() {
		String val = getValue();
		return "[" + getClass().getSimpleName() + (val.length() > 0 ? " " + getValue() : "") + "]";
	}

	public boolean equals(Token t) {
		return t == this;
		// return t.getClass().equals(getClass());
	}
}
