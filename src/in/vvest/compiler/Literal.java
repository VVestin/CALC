package in.vvest.compiler;

import java.util.List;

public class Literal extends Token {
	private static final String[] HEXITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"}; // Hex digits

	private Type type;
	private String value;

	public Literal(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	public void compile(List<String> code) {
		if (type == Type.INTEGER) {
			int number = 0;
			for (int i = 0; i < value.length(); i++) {
				number *= 10;
				number += value.charAt(i) - 48;
			}
			String[] hex = new String[6];
			for (int i = 0; i < hex.length; i++)
				hex[i] = "0";
			int digit = 0;
			while (number > 0) {
				hex[digit] = HEXITS[number % 16];
				number /= 16;
				digit++;
			}
			code.add("ld a,$" + hex[1] + hex[0]);
			code.add("ld de,$" + hex[5] + hex[4] + hex[1] + hex[2]);
			code.add("call PushIntLiteral");
		}
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return value;
	}
}
