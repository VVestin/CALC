package in.vvest.compiler;

import java.util.List;

public class Command extends Token {

	private String command;

	public Command(String command) {
		super();
		this.command = command;
	}

	public void compile(List<String> code) {
		code.add("call " + command);
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return command;
	}

	public Token copy() {
		return new Command(command);
	}
}
