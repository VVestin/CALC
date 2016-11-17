package in.vvest.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import in.vvest.lexer.Lexer;
import in.vvest.lexer.Token;
import in.vvest.parser.Parser;
import in.vvest.parser.TreeNode;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner src; 
		src = new Scanner(new File("res/Theta4.txt"));
		//src = new Scanner("1+2->A:30*1->B");
		Lexer lex = new Lexer(src);
		List<Token> tokens = lex.tokenize();
		System.out.println(tokens);
		TreeNode ast = Parser.parse(tokens);
		System.out.println("\nPrinting AST:");
		ast.print();
	}

}
