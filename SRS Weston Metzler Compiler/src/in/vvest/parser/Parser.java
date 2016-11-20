package in.vvest.parser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;

import in.vvest.lexer.TI84Token;
import in.vvest.lexer.Token;
import in.vvest.lexer.TokenClass;

public class Parser {

	private static final Set<TokenClass> VALUE_TOKENS = new HashSet<TokenClass>(Arrays.asList(TokenClass.FIXED_LITERAL, TokenClass.FIXED_VAR, TokenClass.INT_LITERAL, TokenClass.INT_VAR,TokenClass.STRING_LITERAL, TokenClass.STRING_VAR, TokenClass.MATRIX_VAR));
	private static final Set<Token> CONTROLL_STRUCTS = new HashSet<Token>(Arrays.asList(TI84Token.IF.getToken(), TI84Token.WHILE.getToken(), TI84Token.REPEAT.getToken(), TI84Token.FOR.getToken()));
	private static final Set<String> BINARY_OPS = new HashSet<String>(	Arrays.asList("+", "-", "/", "*", "^", "and", "or", "xor", "->", "=", "=/=", "<", ">", "<=", ">="));

	// Higher precedence is a lower index
	private static final String[][] OPERATOR_PRECEDENCE = { { "_" }, { "*", "/" }, { "+", "-" },
			{ "<", "<=", ">=", ">" }, { "=", "=/=" }, { "and" }, { "or" } };

	public static TreeNode parse(List<Token> src) {
		Stack<Token> operatorStack = new Stack<Token>();
		Stack<TreeNode> rpn = new Stack<TreeNode>();
		Iterator<Token> it = src.iterator();
		boolean newLine = false;
		boolean endLine = false;
		while (it.hasNext()) {
			Token next = it.next();
			if (next.getType() == TokenClass.COLON) {
				newLine = true;
				endLine = false;
				operatorStack.push(next);
				continue;
			} else if (endLine) { 
				System.err.println("Parse Error. Instructions must be on their own lines");
			} else if (next.getType() == TokenClass.STO) {
				while (operatorStack.peek().getType() != TokenClass.COLON)
					pushOperator(rpn, operatorStack.pop());
				operatorStack.pop();
				if (!it.hasNext())
					System.err.println("Parse Error. Assignment operator has no right side.");
				pushOperator(rpn, it.next());
				pushOperator(rpn, next);
				endLine = true;
			} else if (VALUE_TOKENS.contains(next.getType())) {
				rpn.push(new TreeNode(next));
			} else if (next.getType() == TokenClass.OPERATOR || next.getType() == TokenClass.NEGATIVE) {
				while (!operatorStack.isEmpty() && higherPriority(operatorStack.peek().getValue(), next.getValue())) {
					pushOperator(rpn, operatorStack.pop());
				}
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.OPEN_PAREN) {
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.FUNCTION || CONTROLL_STRUCTS.contains(next)) {
				rpn.push(new TreeNode(new MarkerToken(next.getType(), next.getValue())));
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.CLOSE_PAREN) {
				while (!(operatorStack.peek().getType() == TokenClass.OPEN_PAREN
						|| operatorStack.peek().getType() == TokenClass.FUNCTION)) {
					pushOperator(rpn, operatorStack.pop());
				}
				Token openToken = operatorStack.pop(); // Token which matches the close paren
				if (openToken.getType() == TokenClass.FUNCTION)
					pushOperator(rpn, openToken);
			} else if (next.equals(TI84Token.END.getToken())) {
				while (!CONTROLL_STRUCTS.contains(operatorStack.peek())) {
					pushOperator(rpn, operatorStack.pop());
				}
				pushOperator(rpn, operatorStack.pop());
				endLine = true;
			} else if (next.getType() == TokenClass.COMMAND) {
				if (!newLine) {
					System.err.println("Parse Error. Cannot execute " + next + " inline");
				} else {
					pushOperator(rpn, next);
					endLine = true;
				}
			}
			newLine = false;
		}
		while (!operatorStack.isEmpty()) {
			pushOperator(rpn, operatorStack.pop());
		}
		TreeNode program = new TreeNode(null);
		while (!rpn.isEmpty()) {
			program.children.add(rpn.pop());
		}
		program.reverseChildren();
		return program;
	}

	private static void pushOperator(Stack<TreeNode> stack, Token operator) {
		TreeNode n = new TreeNode(operator);
		if (BINARY_OPS.contains(operator.getValue())) {
			n.children.add(stack.pop());
			n.children.add(stack.pop());
		} else if (operator.getType() == TokenClass.NEGATIVE) {
			n.children.add(stack.pop());
		} else if (operator.getType() == TokenClass.FUNCTION || CONTROLL_STRUCTS.contains(operator)) {
			while (!(stack.peek().t.equals(operator) && (stack.peek().t instanceof MarkerToken)))
				n.children.add(stack.pop());
			stack.pop();
		} else if (operator.getType() == TokenClass.COLON) {
			return;
		}
		stack.push(n);
	}

	private static boolean higherPriority(String a, String b) {
		for (int i = 0; i < OPERATOR_PRECEDENCE.length; i++) {
			boolean aFound = false;
			boolean bFound = false;
			for (int j = 0; j < OPERATOR_PRECEDENCE[i].length; j++) {
				if (a.equals(OPERATOR_PRECEDENCE[i][j])) {
					aFound = true;
				}
				if (b.equals(OPERATOR_PRECEDENCE[i][j])) {
					bFound = true;
				}
			}
			if (bFound)
				return false;
			if (aFound)
				return true;
		}
		System.err.println("Parse Error. " + a + " and " + b + " are not known operators");
		return false;
	}
}
