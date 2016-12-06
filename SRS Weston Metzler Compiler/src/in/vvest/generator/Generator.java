package in.vvest.generator;

import java.util.ArrayList;
import java.util.List;

import in.vvest.lexer.TI84Token;
import in.vvest.lexer.TokenClass;
import in.vvest.parser.TreeNode;

public class Generator {
	private static final String[] HEXITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		
	public static List<String> generateCode(TreeNode prgm) {
		List<String> code = new ArrayList<String>();
		for (TreeNode statement : prgm.getChildren()) {
			gen(statement, code);
		}
		return code;
	}
	
	private static void gen(TreeNode t, List<String> code) {
		System.out.println("GENERATING " + t.getToken());
		if (t.getToken().getType() == TokenClass.INT_LITERAL) {
			pushBytes(decToHex(t.getToken().getValue()), code);
		} else if (t.getToken().equals(TI84Token.DISP.getToken())) {
			for (TreeNode child : t.getChildren()) {
				gen(child, code);
			}
			code.add("call Disp");
		}
	}
	
	private static void pushBytes(String[] bytes, List<String> code) {
//		ld hl,TempPtr
//		ld (hl),$2C
//		inc hl
//		ld (hl),$01
//		inc hl
//		ld (hl),$00
//		inc hl
//		ld (hl),$00
//		dec	hl
//	    dec hl
//	    dec hl
//	    push hl
//		ld hl,TempPtr
//	    ld a,4
//	    add a,(hl)
//		ld (hl),a
	    String tempPtr = "TempPtr";
	    code.add("ld hl," + tempPtr);
	    for (int i = bytes.length - 1; i >= 0; i--) {
	    	code.add("ld (hl),$" + bytes[i]);
	    	code.add("inc hl");
	    }
	    code.set(code.size() - 1, "dec hl");
	    code.add("dec hl");
	    code.add("dec hl");
	    code.add("push hl");
	    code.add("ld hl," + tempPtr);
	    code.add("ld a,4");
	    code.add("add a,(hl)");
	    code.add("ld (hl),a");
	    
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
