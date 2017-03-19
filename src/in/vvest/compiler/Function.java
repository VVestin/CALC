package in.vvest.compiler;

import java.util.List;

public class Function extends Token {
	public static final int VARIADIC_ARITY = -1; // A -1 represents a variadic function

	private String label;
	private int arity;
	private Type returnType;

	public Function(String label, int arity, Type returnType) {
		super();
		this.label = label;
		this.arity = arity;
		this.returnType = returnType;
	}

	public void compile(List<String> code) {
		if (children.size() != arity && arity != VARIADIC_ARITY) System.err.println("Compile Error. Too many arguments to " + label);
		for (Token t : children) {
			t.compile(code);
		}
		code.add("call " + label);
	}

	public String getValue() {
		return label;
	}

	public int getArity() {
		return arity;
	}

	public boolean isCompileable() {
		return true;
	}

	public Type getType() {
		return returnType;
	}

	public Token copy() {
		return new Function(label, arity, returnType);
	}

	public static class Disp extends Function {
		public Disp() {
			super("Disp", VARIADIC_ARITY, Type.VOID);
		}
		public void compile(List<String> code) {
			if (children.size() == 0) code.add("bcall(_NewLine)");
			for (Token child : children) {
				if (child.getType() == Type.VOID) System.err.println("Compile Error. Arguments to Disp must be expressions");
				child.compile(code);
			}
			for (int i = children.size() - 1; i >= 0; i--) { // Must loop backwards to match the backwardsness of stacks
				code.add("call Disp" + (children.get(i).getType() == Type.INTEGER ? "Num" : ""));
			}
		}
		public Token copy() {
			return new Disp();
		}
	}

	public static class SubString extends Function {
		public SubString() {
			super("SubString", 3, Type.STRING);
		}
		public void compile(List<String> code) {
			if (children.size() != 3) System.err.println("Compiler Error. sub() must have 3 arguments");
			if (children.get(2).getType() != Type.STRING) System.out.println("Compiler Error. First argument to sub() must be string");
			if (children.get(1).getType() != Type.INTEGER) System.out.println("Compiler Error. Second argument to sub() must be integer");
			if (children.get(0).getType() != Type.INTEGER) System.out.println("Compiler Error. Third argument to sub() must be integer");
			children.get(2).compile(code);
			children.get(0).compile(code);
			children.get(1).compile(code);
			code.add("call SubString");
		}
		public Token copy() {
			return new SubString();
		}
	}

	public static class Output extends Function {
		public Output() {
			super("Output", 3, Type.VOID);
		}
		public Output(String s, int i, Type t) {
			super(s, i, t);
		}
		public void compile(List<String> code) {
			if (children.size() != getArity()) System.err.println("Compile Error. Output has incorrect arity");
			if (children.get(0).getType() != Type.STRING && children.get(0).getType() != Type.INTEGER) System.err.println("Compiler Error. Arguments to Ouput have incorrect types");
			if (children.get(1).getType() != Type.INTEGER) System.err.println("Compiler Error. Arguments to Ouput have incorrect types");
			if (children.get(2).getType() != Type.INTEGER) System.err.println("Compiler Error. Arguments to Ouput have incorrect types");
			children.get(0).compile(code);
			if (children.get(0).getType() == Type.INTEGER) code.add("call Num2Str");
			children.get(1).compile(code);
			children.get(2).compile(code);
			code.add("call Output");
		}
		public Token copy() {
			return new Output();
		}
	}

	public static class OutputS extends Output {
		public OutputS() {
			super("OutputS", 3, Type.VOID);
		}

		public void compile(List<String> code) {
			code.add("set TextInverse,(IY + TextFlags)");
			super.compile(code);
			code.add("res TextInverse,(IY + TextFlags)");
		}

		public Token copy() {
			return new OutputS();
		}
	}

	public static class Pop extends Function {
		public Pop() {
			super("Pop", 1, Type.INTEGER);
		}
		public void compile(List<String> code) {
			if (children.size() != getArity()) System.err.println("Compile Error. Pop has incorrect arity");
			if (!(children.get(0) instanceof Identifier)) System.err.println("Compiler Error. Pop must be passed a List identifier");
			Identifier var = ((Identifier) children.get(0));
			if (var.getType() != Type.LIST) System.err.println("Compiler Error. Pop must be passed a List identifier");
			code.add("ld de,(ListVar+" + 2 * (var.getIdentifier().charAt(1) - '0') + ")"); 
			code.add("call Pop");
		}
		public Token copy() {
			return new Pop();
		}
	}

	public static class FunCall extends Function {
		private ControlStructure.FunDef fun;
		private String id;
		public FunCall() {
			super("Fun", VARIADIC_ARITY, Type.VOID);
		}
		public FunCall(String id) {
			this();
			this.id = id;
		}
		public void compile(List<String> code) {
			code.add("call " + fun.getAddress() + "Pre");
			for (Token t : children) {
				t.compile(code);
			}
			code.add("call " + fun.getAddress());				
		}
		public Type getType() {
			return fun.getType();
		}
		public String getValue() {
			return id;
		}
		public void setFun(ControlStructure.FunDef fun) {
			this.fun = fun;
		}
		public String getID() {
			return id;
		}
		public Token copy() {
			return new FunCall();
		}
	}

	public static class Return extends Function {
		private String id;
		public Return() {
			super("Return", 1, Type.VOID);
		}
		public void compile(List<String> code) {
			if (children.size() == 1)
				children.get(0).compile(code);
			code.add("jp FunReturn" + id); 
		}
		public void setID(String id) {
			this.id = id;
		}
		public Type getType() {
			if (children.size() == 0) 
				return Type.VOID;
			return children.get(0).getType();
		}
		public Token copy() {
			return new Return();
		}
	}

	public static class Include extends Function {
		public Include() {
			super("Include", 1, Type.VOID);
		}
		public Token copy() {
			return new Include();
		}
	}
}
