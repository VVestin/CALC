package in.vvest.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Compiler {

	private Lexer lex;
	private Parser parser;

	public Compiler() {
		Map<String, Token> tokenID = new HashMap<String, Token>();
		tokenID.put("+", new Operator.Add());
		tokenID.put("-", new Operator.Subtract());
		tokenID.put("*", new Operator.Multiply());
		tokenID.put("/", new Operator.Divide());
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
		tokenID.put("Disp(", new Function("Disp"));
		tokenID.put("min(", new Function("Min"));
		tokenID.put("max(", new Function("Max"));
		tokenID.put("If(", new ControlStructure.If());
		tokenID.put("While(", new ControlStructure.While());
		tokenID.put("Repeat(", new ControlStructure.Repeat());
		tokenID.put("For(", new ControlStructure.For());
		tokenID.put("End", new ControlStructure.End());
		tokenID.put(":", new Colon());
		tokenID.put(",", new Comma());
		tokenID.put("(", new Parenthesis(true));
		tokenID.put(")", new Parenthesis(false));
		lex = new Lexer(tokenID);
		parser = new Parser();
	}

	public void compile(File f) throws FileNotFoundException {
		compile(new Scanner(f));
	}

	public void compile(Scanner s) {
		String src = "";
		while (s.hasNextLine()) {
			src += ":" + s.nextLine();
		}
		s.close();
		compile(src);
	}

	public void compile(String src) {
		System.out.println(src.replace(":", "\n"));
		List<Token> tokens = lex.tokenize(src);
		Token prgm = parser.parse(tokens);
		prgm.print();
	}

	public static void main(String[] args) throws FileNotFoundException {
		Compiler c = new Compiler();
		c.compile(new File("res/Theta0.txt"));
	}

}
