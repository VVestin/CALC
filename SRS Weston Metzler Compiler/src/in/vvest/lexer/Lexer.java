package in.vvest.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Lexer {

	private String src;

	public Lexer(File f) {
		src = "";
		try {
			Scanner s = new Scanner(f);
			while (s.hasNextLine()) {
				src += s.nextLine() + ":";
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<Token>();
		Map<String, TokenClass> singleCharToks = new TreeMap<String, TokenClass>();
		singleCharToks.put("+", TokenClass.OPERATOR);
		singleCharToks.put("-", TokenClass.OPERATOR);
		singleCharToks.put("*", TokenClass.OPERATOR);
		singleCharToks.put("/", TokenClass.OPERATOR);
		singleCharToks.put("^", TokenClass.OPERATOR);
		singleCharToks.put("=", TokenClass.OPERATOR);
		singleCharToks.put("<", TokenClass.OPERATOR);
		singleCharToks.put(">", TokenClass.OPERATOR);
		singleCharToks.put("_", TokenClass.NEGATIVE);
		singleCharToks.put(",", TokenClass.COMMA);
		singleCharToks.put(":", TokenClass.COLON);
		singleCharToks.put("(", TokenClass.OPEN_PAREN);
		singleCharToks.put(")", TokenClass.CLOSE_PAREN);
		System.out.println(src);
		while (src.length() > 0) {
			// Tokenizes the symbols in the src code that would normally be
			// tokens on the TI-84
			// In ASM, might have to replace with something similiar to assign a
			// TokenClass
			for (TI84Token s : TI84Token.values()) {
				if (src.startsWith(s.getText())) {
					tokens.add(s.getToken());
					src = src.substring(s.getText().length());
					continue;
				}
			}
			if (src.startsWith("\"")) {
				// Tokenizes String Literals
				// Important Difference! TI84 Tokens within strings will not be
				// tokenized
				// within a string literal (unlike TI-BASIC onCalc)
				int strEnd = src.indexOf("\"", 1);
				if (src.indexOf(":") < strEnd || strEnd == -1)
					strEnd = src.indexOf(";");
				if (src.indexOf("->") < strEnd || strEnd == -1)
					strEnd = src.indexOf("->");
				if (strEnd == -1)
					strEnd = src.length();
				tokens.add(new Token(TokenClass.STRING_LITERAL, src.substring(0, strEnd)));
				src = src.substring(strEnd);
			} else if (src.startsWith(".") || isNum(src.charAt(0))) {
				boolean decimal = false;
				boolean number = false;
				int index = 0;
				while (src.charAt(index) == '.' || isNum(src.charAt(index))) {
					if (src.charAt(index) == '.') {
						if (decimal)
							// TODO add line numbers to exceptions
							throw new RuntimeException(
									"Lexical Analysis failed. Multiple decimal points in a Number Literal: ");
						decimal = true;
					} else {
						number = true;
					}
					index++;
				}
				if (!number)
					throw new RuntimeException("Lexical Analysis failed. Unable to lex single '.'");
				TokenClass numType = TokenClass.INT_LITERAL;
				if (decimal)
					numType = TokenClass.FIXED_LITERAL;
				tokens.add(new Token(numType, src.substring(0, index)));
				src = src.substring(index);
			} else if (src.startsWith("Str")) {
				if (isNum(src.charAt(3)))
					throw new RuntimeException(
							"Lexical Analysis Failed. Unsupported String literal " + src.substring(0, 4));
				tokens.add(new Token(TokenClass.STRING_LITERAL, src.substring(0, 4)));
				src = src.substring(4);
			} else if (src.startsWith("[")) {
				// TODO support matrices
				throw new RuntimeException("Lexical Analysis Failed. Matrices not supported");
			} else if (isUpperCaseLetter(src.charAt(0))) {
				tokens.add(new Token(TokenClass.INT_VAR, src.substring(0, 1)));
				src = src.substring(1);
			} else if (isLowerCaseLetter(src.charAt(0))) {
				tokens.add(new Token(TokenClass.FIXED_VAR, src.substring(0, 1)));
				src = src.substring(1);
			} else if (src.startsWith("->")) {
				tokens.add(new Token(TokenClass.STO, src.substring(0, 2)));
				src = src.substring(2);
			} else if (singleCharToks.containsKey(src.substring(0, 1))) {
				tokens.add(new Token(singleCharToks.get(src.substring(0, 1)), src.substring(0, 1)));
				src = src.substring(1);
			} else {
				System.err.println("Unable to lex: " + src.charAt(0) + ". Continuing with rest of input");
				src = src.substring(1);
			}
		}
		return tokens;
	}

	private boolean isNum(char c) {
		return c >= 48 && c <= 57; // Will also work on calc because TI-83 uses
									// mostly standard ASCII
	}

	private boolean isUpperCaseLetter(char c) {
		return c >= 65 && c <= 90;
	}

	private boolean isLowerCaseLetter(char c) {
		return c >= 97 && c <= 122;
	}
}
