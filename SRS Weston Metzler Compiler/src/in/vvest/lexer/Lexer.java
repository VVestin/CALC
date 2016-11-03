package in.vvest.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {

	private String src;
	
	public Lexer(File f) {
		try {
			Scanner s = new Scanner(f);
			while (s.hasNextLine())
				src += s.nextLine();
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<Token>();
		while (src.length() > 0) {
			// Tokenizes the symbols in the src code that would normally be tokens on the TI-84
			// In ASM, might have to replace with something similiar to assign a TokenClass
			for (TI84Token s : TI84Token.values()) {
				if (src.startsWith(s.getText())) {
					tokens.add(s.getToken());
					src = src.substring(s.getText().length());
					continue;
				}
			}
			if (src.startsWith("\"")) {
				// Tokenizes String Literals
				// Important Difference! TI84 Tokens within strings will not be tokenized
				// within a string literal (unlike TI-BASIC onCalc)
				int strEnd = src.indexOf("\"", 1);
				if (src.indexOf("\n") < strEnd) strEnd = src.indexOf("\n");
				tokens.add(new Token(TokenClass.STRING_LITERAL, src.substring(0, strEnd)));
				src = src.substring(strEnd);
			} else if (src.startsWith(".") || isNum(src.charAt(0))) {
				boolean decimal;
				boolean number;
				int index = 0;
				while (src.charAt(index) == '.' || isNum(src.charAt(index))) {
					if (src.charAt(index) == '.') {
						if (decimal)
							// TODO add line numbers to exceptions
							throw new RuntimeException("Lexical Analysis failed. Single '.' cannot be tokenized on line: ");
						decimal = true;
					} else {
						number = true;
					}
					index++;
				}
				TokenClass numType = TokenClass.INT_LITERAL;
				if (decimal) numType = TokenClass.FIXED_LITERAL;
				tokens.add(new Token(numType, src.substring(0, index)));
				src = src.substring(index);
			}
		}
		return tokens;
	}
	
	private boolean isNum(char c) {
		return c >= 48 && c <= 57;
	}
	
}
