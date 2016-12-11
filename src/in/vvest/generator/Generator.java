package in.vvest.generator;

import java.util.ArrayList;
import java.util.List;

import in.vvest.lexer.TI84Token;
import in.vvest.lexer.TokenClass;
import in.vvest.parser.TreeNode;

public class Generator {
	private static final String[] HEXITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
			"F" };
	private static int labelIdentifier = 0;
	
	public static List<String> generateCode(TreeNode prgm) {
		List<String> code = new ArrayList<String>();
		for (TreeNode statement : prgm.getChildren()) {
			gen(statement, code);
		}
		code.add("ret");
		return code;
	}

	private static void gen(TreeNode t, List<String> code) {
		System.out.println("GENERATING " + t.getToken());
		if (t.getToken().getType() == TokenClass.INT_LITERAL) {
			pushBytes(decToHex(t.getToken().getValue()), code);
		} else if (t.getToken().equals(TI84Token.DISP.getToken())) {
			for (TreeNode child : t.getChildren()) {
				gen(child, code);
				code.add("call Disp");
				code.add("");
			}
		} else if (t.getToken().getType() == TokenClass.OPERATOR) {
			if (t.getChildren().size() != 2)
				System.err.println("Generation Error. Operator has too many operands somehow.");
			for (TreeNode child : t.getChildren()) {
				gen(child, code);
			}
			if (t.getToken().getValue().equals("+")) {
				code.add("call Add");
			}
		} else if (t.getToken().equals(TI84Token.STO.getToken())) {
			System.out.println("Storing to a variable: ");
			if (t.getChildren().size() != 2)
				System.err.println("Generation Error. Operator has too many operands somehow.");
			gen(t.getChildren().get(0), code);
			code.add("pop hl");
			code.add("ld de,IntVar + " + 4 * (t.getChildren().get(1).getToken().getValue().charAt(0) - 'A'));
			moveHLtoDE(4, code);
		} else if (t.getToken().getType() == TokenClass.INT_VAR) {
			code.add("ld de,IntVar + " + 4 * (t.getToken().getValue().charAt(0) - 'A'));
			code.add("call LoadIntVar");
		}
	}
	
	private static void moveHLtoDE(int bytes, List<String> code) {
		code.add("ld b," + bytes);
		code.add("_MoveBytesLoop" + ++labelIdentifier + ":");
		code.add("ld a,(hl)");
		code.add("ld (de),a");
		code.add("inc hl");
		code.add("inc de");
		code.add("djnz _LoadBytesLoop" + labelIdentifier);
	}

	private static void pushBytes(String[] bytes, List<String> code) {
//		ld hl,(TempPtr) 
//		ld (hl),$0E 
//	 	inc hl 
//		ld (hl),$01 
//		inc hl 
//		ld (hl),$00 
//		inc hl 
//		ld (hl),$00 
//		dec hl 
//		dec hl 
//		dec hl 
//		push hl 
//		ld de,(TempPtr) 
//		ld h,0 
//		ld l,4 
//		add hl,de 
//		ld (TempPtr),hl
		String tempPtr = "TempPtr";
		code.add("ld hl,(" + tempPtr + ")");
		for (int i = bytes.length - 1; i >= 0; i--) {
			code.add("ld (hl),$" + bytes[i]);
			code.add("inc hl");
		}
		code.set(code.size() - 1, "dec hl");
		code.add("dec hl");
		code.add("dec hl");
		code.add("push hl");
		code.add("ld de," + tempPtr);
		code.add("ld h,0");
		code.add("ld l,4");
		code.add("add hl,de");
		code.add("ld (TempPtr),hl");

	}

	public static String[] decToHex(String dec) {
		long number = 0;
		for (int i = 0; i < dec.length(); i++) {
			number *= 10;
			number += dec.charAt(i) - 48;
		}
		String[] hex = new String[4];
		for (int digit = 0; digit < 8; digit++) {
			if (hex[3 - digit / 2] != null)
				hex[3 - digit / 2] = HEXITS[(int) (number % 16)] + hex[3 - digit / 2];
			else
				hex[3 - digit / 2] = HEXITS[(int) (number % 16)];
			number /= 16;
		}
		return hex;
	}

}
