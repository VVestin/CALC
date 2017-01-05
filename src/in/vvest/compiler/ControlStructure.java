package in.vvest.compiler;

import java.util.List;

public abstract class ControlStructure extends Token {
	public void compile(List<String> code) {

	}

	public boolean isCompileable() {
		return true;
	}

	public void reverseChildren() {
		for (Token child : children) {
			child.reverseChildren();
		}
	}

	public static class FunDef extends ControlStructure {
		private String id;
		public void compile(List<String> code) {
			// push the values held in arg vars on to stack and swap them with passed in values
			// generate block code
			// if there is a return load return value into a special location in memory and jump to return code
			// pop values off stack and restore them into their vars	
		}
		public void setID(String id) {
			this.id = id;
		}
		public int getArity() {
			return children.size() - 1;
		}
		public Type getReturnType() {


		}
		public String getValue() {
			return id != null ? id : "";
		}
		public String getLabel() {
			return "Fun" + id;
		}
		public Token copy() {
			return new FunDef();
		}
	}

	public static class For extends ControlStructure {
		// TODO allow looping backwards.
		// Weird thing TI-BASIC does is it evaluates the step once at the beginning
		// And then uses that value (and wether it is positive) forever
		// Also, a value of 0 causes an infinit loop error in TI-BASIC
		// Consider doing a C-like loop with actual init, condition, and update statements
		private static int labelIdentifier = 0;
		public void compile(List<String> code) {
			if (children.size() != 4 && children.size() != 5 || !(children.get(children.size() - 1) instanceof Block))
				System.err.println("Compiler Error. For must have an arity of 3 or 3 and a loop body.");
			if (!(children.get(0) instanceof Identifier) || ((Identifier) children.get(0)).getType() != Type.INTEGER)
				System.err.println("Compile Error. For must loop over an Integer variable");
			String forLabel = "ForLoop" + ++labelIdentifier;
			String forEndLabel = "ForLoopEnd" + labelIdentifier;
			Operator.Store init = new Operator.Store();
			init.children.add(children.get(1));
			init.children.add(children.get(0));
			init.compile(code);
			code.add(forLabel + ":");
			Operator.Compare compare = new Operator.Compare(true, true); // Compare <= Loop var and children.get(2)
			compare.children.add(children.get(0));
			compare.children.add(children.get(2));
			compare.compile(code);
			code.add("ld hl," + forEndLabel);
			code.add("call If");
			children.get(children.size() - 1).compile(code); // Execute loop body
			Operator.Add updateCounter = new Operator.Add();
			updateCounter.children.add(children.get(0));
			if (children.size() == 4) {
				updateCounter.children.add(new Literal(Type.INTEGER, "1"));
			} else {
				updateCounter.children.add(children.get(3));
			}
			Operator.Store update = new Operator.Store();
			update.children.add(updateCounter);
			update.children.add(children.get(0));
			update.compile(code);
			code.add("jp " + forLabel);
			code.add(forEndLabel + ":");
		}
		public Token copy() {
			return new For();
		}
	}

	public static class While extends ControlStructure {
		private static int labelIdentifier = 0;
		public void compile(List<String> code) {
			if (children.size() != 2 || !(children.get(1) instanceof Block))
				System.err.println("Compile Error. While must have arity of 1 and a loop body");
			String whileLabel = "While" + ++labelIdentifier;	
			String whileEndLabel = "WhileEnd" + labelIdentifier;
			code.add(whileLabel + ":");
			children.get(0).compile(code);
			code.add("ld hl," + whileEndLabel);
			code.add("call If"); // This works because if false, it will go to whileEndLabel
			children.get(1).compile(code);
			code.add("jp " + whileLabel);
			code.add(whileEndLabel + ":");
		}
		public Token copy() {
			return new While();
		}
	}

	public static class If extends ControlStructure {
		private static int ifLabel = 0;
		public void compile(List<String> code) {
			if (children.size() < 2) System.err.println("Compile Error. If must have at least 2 children.");
			if (children.get(0).getType() != Type.INTEGER) System.err.println("Compile Error. If must have an integer condition");
			String label = "If" + ifLabel;
			String endLabel = "IfEnd" + ifLabel;
			children.get(0).compile(code);
			code.add("ld hl," + label);
			code.add("call if");
			for (int i = 1; i < children.size(); i++) {
				if (children.get(i) instanceof Block) {
					children.get(i).compile(code);
					code.add("jp " + endLabel);
					code.add(label + ":");
					label = "If" + ++ifLabel;
				} else if (children.get(i) instanceof ControlStructure.ElseIf) {
					if (children.get(i).children.size() != 1) System.err.println("Compile Error. If must have only 1 condition");
					children.get(i).children.get(0).compile(code);
					code.add("ld hl," + label);
					code.add("call If");
				} else if (children.get(i) instanceof ControlStructure.Else) {
					// I don't think I have to do anything
				}	
			}
			code.add(endLabel + ":");
		}
		public Token copy() {
			return new If();
		}
	}

	public static class Repeat extends ControlStructure {
		private static int labelIdentifier = 0;
		public void compile(List<String> code) {
			if (children.size() != 2 || !(children.get(1) instanceof Block))
				System.err.println("Compile Error. Repeat must have arity of 1 and a loop body");
			String repeatLabel = "RepeatLoop" + ++labelIdentifier;
			code.add(repeatLabel + ":");
			children.get(1).compile(code);
			children.get(0).compile(code);
			code.add("ld hl," + repeatLabel);
			code.add("call If"); // This works because If will check what's on the stack and if it is false jump to HL
		}
		public Token copy() {
			return new Repeat();
		}
	}

	public static class ElseIf extends ControlStructure {
		public Token copy() {
			return new ElseIf();
		}
	}

	public static class Return extends ControlStructure {
		public Token copy() {
			return new ElseIf();
		}
	}

	public static class Else extends ControlStructure { }
	public static class End extends ControlStructure { }
}
