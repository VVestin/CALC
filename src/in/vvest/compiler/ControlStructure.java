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
		public void compile(List<String> code) {

		}
	}

	public static class While extends ControlStructure {
		public void compile(List<String> code) {

		}
	}

	public static class If extends ControlStructure {
		public void compile(List<String> code) {

		}
	}

	public static class Repeat extends ControlStructure {
		public void compile(List<String> code) {

		}
	}

	public static class End extends ControlStructure {
		public void compile(List<String> code) {

		}
	}
}
