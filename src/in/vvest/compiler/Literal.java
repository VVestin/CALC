package in.vvest.compiler;

import java.util.List;

public class Literal extends Token {
	private static final String[] HEXITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"}; // Hex digits
	private static int labelNum = 0;

	private Type type;
	private String value;
	private String dataLabel;

	public Literal(Type type, String value) {
		this.type = type;
		this.value = value;
		if (type == Type.STRING) {
			dataLabel = "String" + ++labelNum;
		}
	}

	public void compile(List<String> code) {
		if (type == Type.INTEGER) {
			if (value.charAt(0) == '$') {
				String hex = value.substring(1);
				for (int i = hex.length(); i < 8; i++)
					hex = "0" + hex;
				code.add("ld bc,$" + hex.substring(6) + hex.substring(4, 6));
				code.add("ld de,$" + hex.substring(2, 4) + hex.substring(0, 2));
			} else {
				int number = 0;
				for (int i = 0; i < value.length(); i++) {
					number *= 10;
					number += value.charAt(i) - 48;
				}
				String[] hex = new String[8];
				for (int i = 0; i < hex.length; i++)
					hex[i] = "0";
				int digit = 0;
				while (number > 0) {
					hex[digit] = HEXITS[number % 16];
					number /= 16;
					digit++;
				}
				code.add("ld bc,$" + hex[1] + hex[0] + hex[3] + hex[2]);
				code.add("ld de,$" + hex[5] + hex[4] + hex[7] + hex[6]);
			}
			code.add("call PushIntLiteral");
		} else if (type == Type.STRING) {
			code.add("ld hl," + dataLabel);
			code.add("call PushStringLiteral");
		} else if (type == Type.LIST) {
			for (int i = children.size() - 1; i >= 0; i--) {
				children.get(i).compile(code);
			}
			code.add("ld a," + children.size());
			code.add("call PushListLiteral");
		}
	}

	public void addData(List<String> code) {
		if (type == Type.STRING) {
			code.add(dataLabel + ":");
			code.add(".db " + (value.length() - 2) + "," + value); // Value must be enclosed within double quotes for this to work
		}
	}

	public boolean isCompileable() {
		return true;
	}

	public String getValue() {
		return value.length() > 0 ? value : type.toString();
	}

	public Type getType() {
		return type;
	}

	public static class Rand extends Literal {
		public Rand() {
			super(Type.INTEGER, "Rand");
		}
		public void compile(List<String> code) {
			code.add("call Rand");
		}
	}
	
	public static class GetKey extends Literal {
		public GetKey() {
			super(Type.INTEGER, "GetKey");
		}
		public void compile(List<String> code) {
			code.add("call GetKey");
		}
	}

	public static class GetTime extends Literal {
		public GetTime() {
			super(Type.INTEGER, "GetTime");
		}
		public void compile(List<String> code) {
			code.add("call GetTime");
		}
	}
}
