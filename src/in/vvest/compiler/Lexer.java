package in.vvest.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Lexer {
	private static final String COMMENT_CHARACTER = "?";
	private Map<String, Token> tokenIdentifiers;

	public Lexer() {
		Map<String, Token> tokenID = new HashMap<String, Token>();
		tokenID.put("+", new Operator.Add());
		tokenID.put("-", new Operator.Subtract());
		tokenID.put("*", new Operator.Multiply());
		tokenID.put("/", new Operator.Divide());
		tokenID.put("%", new Operator.Modulo());
		tokenID.put("<", new Operator.Compare(true, false));
		tokenID.put(">", new Operator.Compare(false, false));;
		tokenID.put("<=", new Operator.Compare(true, true));
		tokenID.put(">=", new Operator.Compare(false, true));
		tokenID.put("=", new Operator.Equal(false));
		tokenID.put("=/=", new Operator.Equal(true));
		tokenID.put(" and ", new Operator.AND());
		tokenID.put(" or ", new Operator.OR());
		tokenID.put(" xor ", new Operator.XOR());
		tokenID.put("->", new Operator.Store());
		tokenID.put("Disp(", new Function.Disp());
		tokenID.put("Output(", new Function.Output());
		tokenID.put("not(", new Function("Not", 1, Type.INTEGER));
		tokenID.put("str(", new Function("Num2Str", 1, Type.STRING));
		tokenID.put("sub(", new Function.SubString());
		tokenID.put("dim(", new Function("Dimension", 1, Type.INTEGER));
		tokenID.put("augment(", new Function("Augment", 2, Type.LIST));
		tokenID.put("pop(", new Function.Pop());
		tokenID.put("Return(", new Function.Return()); // TODO make this a control structure.
		tokenID.put("Include(", new Function.Include());
		tokenID.put("If(", new ControlStructure.If());
		tokenID.put("Else", new ControlStructure.Else());
		tokenID.put("ElseIf(", new ControlStructure.ElseIf());
		tokenID.put("While(", new ControlStructure.While());
		tokenID.put("Repeat(", new ControlStructure.Repeat());
		tokenID.put("For(", new ControlStructure.For());
		tokenID.put("Fun ", new ControlStructure.FunDef());
		tokenID.put("End", new ControlStructure.End());
		tokenID.put("rand", new Literal.Rand());
		tokenID.put("getKey", new Literal.GetKey());
		tokenID.put(":", new Colon());
		tokenID.put(",", new Comma());
		tokenID.put("(", new Parenthesis(true));
		tokenID.put(")", new Parenthesis(false));
		tokenID.put("{", new ListBrace(true));
		tokenID.put("}", new ListBrace(false));
		tokenID.put("[", new ListBracket(true));
		tokenID.put("]", new ListBracket(false));
		tokenID.put("Pause", new Command("Pause"));
		tokenID.put("Stop", new Command("Stop"));
		tokenID.put("ClrHome", new Command("ClrHome"));
		tokenIdentifiers = tokenID;
	}

	public List<Token> tokenize(String src) {
		System.out.println("Parsing \n" + src);
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
				// I also went ahead and took out using -> to stop strings
				int strEnd = src.indexOf("\"", 1);
				if (strEnd == -1)
					System.err.println("Syntax Error. String missing end quotation mark");
				strEnd++;
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
				tokens.add(new Identifier(Type.STRING, src.substring(0, 4)));
				src = src.substring(4);
			} else if (src.charAt(0) == 'L' && isNum(src.charAt(1))) {
				tokens.add(new Identifier(Type.LIST, src.substring(0, 2)));
				src = src.substring(2);
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
