package in.vvest.compiler;

import java.util.List;

public class Command extends Token {

	private String command;

	public Command(String command) {
		super();
		this.command = command;
	}

	public void compile(List<String> code) {
		if (command.equals("Pause")) {
			code.add("bcall(_GetKey)"); // TODO make it so only [Enter] breaks a pause
		} else if (command.equals("ClrHome")) {
			code.add("bcall(_ClrLCDFull)");
			code.add("bcall(_HomeUp)");
		}

	}

	public boolean isCompileable() {
		return true;
	}

	public Token copy() {
		return new Command(command);
	}
}
