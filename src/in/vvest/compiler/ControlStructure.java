package in.vvest.compiler;

import java.util.List;

public abstract class ControlStructure extends Token {

	public boolean isCompileable() {
		return true;
	}

	public void reverseChildren() {
		for (Token child : children) {
			child.reverseChildren();
		}
	}

	public static class For extends ControlStructure {
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
	}

	public static class If extends ControlStructure {
		private static int labelIdentifier = 0;
		public void compile(List<String> code) {
			if (children.size() != 2 || !(children.get(1) instanceof Block))
				System.err.println("Compiler Error. If must have an arity of 1 and a body");
			children.get(0).compile(code);
			String ifLabel = "IfFalse" + ++labelIdentifier;
			code.add("ld hl," + ifLabel);
			code.add("call If");
			children.get(1).compile(code);
			code.add(ifLabel + ":");
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
	}

	public static class End extends ControlStructure {
		public void compile(List<String> code) {

		}
	}
}
