package in.vvest.main;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import in.vvest.generator.Generator;
import in.vvest.lexer.Lexer;
import in.vvest.lexer.Token;
import in.vvest.parser.Parser;
import in.vvest.parser.TreeNode;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner src; 
		//src = new Scanner(new File("res/Theta3.txt"));
		src = new Scanner("30->A:Disp(A)");
		Lexer lex = new Lexer(src);
		List<Token> tokens = lex.tokenize();
		System.out.println(tokens);
		TreeNode ast = Parser.parse(tokens);
		System.out.println("\nPrinting AST:");
		ast.print();
		System.out.println();
		List<String> code = Generator.generateCode(ast);
		System.out.println("Code:");
		for (String line : code) {
			System.out.println("\t" + line);
		}
	}

	private static void compileWeb(List<String> code) {
		URL url = new URL("http://clrhome.org/asm");
		HttpURLConnection http = (HttpURLConection) url.openConection();
		http.
	}
}
