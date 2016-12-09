package in.vvest.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Note to self: this class is not really a class, 
// just a Java way to make a function. It might be 
// considered bad practice, but it doesn't logically 
// fit into any class, it is a function.
// Also note, it uses lots of lookahead, but that is
// tolerable, because it only requires lookahead for 
// tokens that would already be tokens on the calc.
public class Lexer {
	private static final String COMMENT_CHARACTER = "?";

	private String src;

	public Lexer(String s) {
		this(new Scanner(s));
	}

	public Lexer(File f) throws FileNotFoundException {
		this(new Scanner(f));
	}

	public Lexer(Scanner s) {
		src = "";
		while (s.hasNextLine()) {
			src += ":" + s.nextLine();
		}
		s.close();
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
		// Who know java has labels?
		OuterLoop: while (src.length() > 0) {
			// Tokenizes the symbols in the src code that would normally be
			// tokens on the TI-84
			// In ASM, might have to replace with something similiar to assign a
			// TokenClass
			System.out.println("DEBUG: " + src);
			for (TI84Token s : TI84Token.values()) {
				if (src.startsWith(s.getText())) {
					tokens.add(s.getToken());
					src = src.substring(s.getText().length());
					continue OuterLoop;
				}
			}
			if (src.startsWith("\"")) {
				// Tokenizes String Literals
				// Important Difference! TI84 Tokens within strings will not be
				// tokenized
				// within a string literal (unlike TI-BASIC onCalc)
				int strEnd = src.indexOf("\"", 1);
				if (strEnd != -1)
					strEnd++;
				// TODO fix bug with parsing Strings that have a real : in them, not a line break
				if (src.indexOf(":") < strEnd && src.indexOf(":") != -1 || strEnd == -1)
					strEnd = src.indexOf(":");
				if (src.indexOf("->") < strEnd && src.indexOf("->") != -1 || strEnd == -1)
					strEnd = src.indexOf("->");
				if (strEnd == -1)
					strEnd = src.length();
				tokens.add(new Token(TokenClass.STRING_LITERAL, src.substring(0, strEnd)));
				src = src.substring(strEnd);
			} else if (src.startsWith(":" + COMMENT_CHARACTER)) {
				int commentEnd = src.indexOf(":", 2);
				System.out.println(commentEnd);
				if (commentEnd == -1)
					break;
				src = src.substring(commentEnd);
			} else if (src.startsWith(".") || isNum(src.charAt(0))) {
				boolean decimal = false;
				boolean number = false;
				int index = 0;
				while (index < src.length() && (src.charAt(index) == '.' || isNum(src.charAt(index)))) {
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
				if (!isNum(src.charAt(3)))
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
				System.out.println("\n" + src + "\n");
				System.err.println("Lexical Analysis Failed. Unable to lex: '" + src.charAt(0) + "'. Continuing with rest of input");
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
