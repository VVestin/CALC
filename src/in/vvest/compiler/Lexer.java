package in.vvest.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Lexer {
	private static final String COMMENT_CHARACTER = "?";
	private Map<String, Token> tokenIdentifiers;

	public Lexer(Map<String, Token> tokenIdentifiers) {
		this.tokenIdentifiers = tokenIdentifiers;
	}

	public List<Token> tokenize(String src) {
		List<Token> tokens = new LinkedList<Token>();
		String unrecognized = "";
		while (src.length() > 0) {
			String id = "";
			for (String s : tokenIdentifiers.keySet()) {
				if (s.length() > id.length() && src.startsWith(s)) {
					id = s;
				}
			}
			if (tokenIdentifiers.containsKey(id)) {
				if (unrecognized.length() > 0)
					System.err.println("Parse Error. Unrecognized Token " + unrecognized);
				unrecognized = "";
				tokens.add(tokenIdentifiers.get(id).copy());
				src = src.substring(id.length());
			} else if (src.startsWith("\"")) {
				// Tokenizes String Literals
				// Important Difference! Tokens within strings will not be tokenized
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
				tokens.add(new Literal(Type.STRING, src.substring(0, strEnd)));
				src = src.substring(strEnd);
			} else if (src.startsWith(COMMENT_CHARACTER)) {
				int commentEnd = src.indexOf(":", 2);
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
				// TODO do something with decimals
				tokens.add(new Literal(Type.INTEGER, src.substring(0, index)));
				src = src.substring(index);
			} else if (src.startsWith("Str")) {
				if (!isNum(src.charAt(3)))
					throw new RuntimeException(
							"Lexical Analysis Failed. Unsupported String literal " + src.substring(0, 4));
				tokens.add(new Literal(Type.STRING, src.substring(0, 4)));
				src = src.substring(4);
			} else if (src.startsWith("[")) {
				// TODO support matrices
				throw new RuntimeException("Lexical Analysis Failed. Matrices not supported");
			} else if (isUpperCaseLetter(src.charAt(0))) {
				tokens.add(new Identifier(Type.INTEGER, src.substring(0, 1)));
				src = src.substring(1);
			} else {
				unrecognized += src.substring(0, 1);
				src = src.substring(1);
			}
		}
		return tokens;
	}

	private static boolean isNum(char c) {
		return c >= 48 && c <= 57; // Will also work on calc because TI-83 uses
		// mostly standard ASCII
	}

	private static boolean isUpperCaseLetter(char c) {
		return c >= 65 && c <= 90;
	}

	private static boolean isLowerCaseLetter(char c) {
		return c >= 97 && c <= 122;
	}

}
