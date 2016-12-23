package in.vvest.compiler;

import java.util.List;

public class Operator extends Token {	
		
	private int precedence; // Higher = tighter
	// 0  ->
	// 1  Or, Xor
	// 2  And
	// 3  =, =/=
	// 4  <, >, <=, >=
	// 5  +, -
	// 6  *, /
	// 7  _ (negative)
	
	private Operator(int precedence) {
		super();
		this.precedence = precedence;
	}
	
	public void compile(List<String> code) {
		assert(children.size() == 2);
		for (Token t : children) {
			t.compile(code);
		}
	}
	
	public boolean isHigherPriority(Token o) {
		return !(o instanceof Operator) || precedence > ((Operator) o).precedence;
	}
	
	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return "";
	}

	public static class Store extends Operator {
		private static int labelIdentifier;

		public Store() {
			super(0);
		}

		public void compile(List<String> code) {
			children.get(0).compile(code);
			if (!(children.get(1) instanceof Identifier))
				System.err.println("Error Invalid right sign of assignment");
			Identifier var = (Identifier) children.get(1);
			if (var.getType() == Type.INTEGER) {
				code.add("ld de,IntVar+" + 3 * (var.getIdentifier().charAt(0) - 'A'));
				code.add("call StoIntVar");
			}
		}

		public Token copy() {
			return new Store();
		}
	}

	public static class Negative extends Operator {
		public Negative() {
			super(7);
		}

		public void compile(List<String> code) {
			children.get(0).compile(code);
			code.add("call Negate");
		}
		
		public Token copy() {
			return new Negative();
		}
	}

	public static class Add extends Operator {
		public Add() {
			super(5);
		}		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Add");
		}
		
		public Token copy() {
			return new Add();
		}
	}
	
	public static class Subtract extends Operator {
		public Subtract() {
			super(5);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Negate");
			code.add("call Add");
		}
		
		public Token copy() {
			return new Subtract();
		}
	}
	
	public static class Multiply extends Operator {
		public Multiply() {
			super(6);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Mult");
		}
		
		public Token copy() {
			return new Multiply();
		}
	}

	public static class Divide extends Operator {
		public Divide() {
			super(6);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Divide");
		}
		
		public Token copy() {
			return new Divide();
		}
	}
	
	public static class Equal extends Operator {
		
		private boolean not;
		public Equal(boolean not) {
			super(3);
			this.not = not;
		}
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Equals");
			if (not)
				code.add("call Not");
		}

		public String getValue() {
			return (not ? "!=" : "=");
		}
		
		public Token copy() {
			return new Equal(not);
		}
	}
	
	public static class Compare extends Operator {
		private boolean less, equal;
		
		public Compare(boolean less, boolean equal) {
			super(4);
			this.less = less;
			this.equal = equal;
		}
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("scf");
			if (!less) {
				code.add("ccf");
			}
			code.add("call " + (equal ? "CompareEqual" : "Compare"));
		}

		public String getValue() {
			return (less ? "<" : ">") + (equal ? "=" : "");
		}
		
		public Token copy() {
			return new Compare(less, equal);
		}
	}

	public static class AND extends Operator {
		public AND() {
			super(2);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call And");
		}

		public Token copy() {
			return new AND();
		}
	}
	
	public static class OR extends Operator {
		public OR() {
			super(1);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call Or");
		}

		public Token copy() {
			return new OR();
		}
	}
	
	public static class XOR extends Operator {
		public XOR() {
			super(1);
		}	
		
		public void compile(List<String> code) {
			super.compile(code);
			code.add("call XOr");
		}

		public Token copy() {
			return new XOR();
		}
	}
	
}
