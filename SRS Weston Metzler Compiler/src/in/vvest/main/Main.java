package in.vvest.main;

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
		//src = new Scanner(new File("res/Theta1.txt"));
		src = new Scanner("(2+min(sin(0,2),cos(48+10*2)))/(4*5)");
		Lexer lex = new Lexer(src);
		List<Token> tokens = lex.tokenize();
		System.out.println(tokens);
		TreeNode ast = Parser.parse(tokens);
		ast.print();
	}

}
