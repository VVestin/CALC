package in.vvest.parser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import in.vvest.lexer.Token;
import in.vvest.lexer.TokenClass;

public class Parser {

	private static final Set<TokenClass> VALUE_TOKENS = new TreeSet<TokenClass>(
			Arrays.asList(TokenClass.FIXED_LITERAL, TokenClass.FIXED_VAR, TokenClass.INT_LITERAL, TokenClass.INT_VAR,
					TokenClass.STRING_LITERAL, TokenClass.STRING_VAR, TokenClass.MATRIX_VAR));
	private static final Set<String> BINARY_OPS = new TreeSet<String>(
			Arrays.asList("+", "-", "/", "*", "^", "and", "or", "xor"));
	// Higher precedence is a lower index
	private static final String[][] OPERATOR_PRECEDENCE = { { "*", "/" }, { "+", "-" }, { "<", "<=", ">=", ">" },
			{ "=", "=/=" }, { "and" }, { "or" } };

	public static TreeNode parse(List<Token> src) {
		return parseExpression(src);
	}
	
	private static TreeNode parseExpression(List<Token> expression) {
		Stack<Token> operatorStack = new Stack<Token>();
		Stack<TreeNode> rpn = new Stack<TreeNode>();
		Iterator<Token> it = expression.iterator();
		while (it.hasNext()) {
			Token next = it.next();
			if (VALUE_TOKENS.contains(next.getType())) {
				rpn.push(new TreeNode(next));
			} else if (next.getType() == TokenClass.OPERATOR){
				while (!operatorStack.isEmpty() && higherPriority(operatorStack.peek().getValue(), next.getValue())) {
					pushOperator(rpn, operatorStack.pop());
				}
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.OPEN_PAREN) {
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.FUNCTION || 
					next.getType() == TokenClass.NEGATIVE) {
				rpn.push(new TreeNode(next));
				operatorStack.push(next);
			} else if (next.getType() == TokenClass.CLOSE_PAREN) {
				while (!(operatorStack.peek().getType() == TokenClass.OPEN_PAREN || 
						operatorStack.peek().getType() == TokenClass.FUNCTION)) {
					pushOperator(rpn, operatorStack.pop());
				}
				Token openToken = operatorStack.pop(); // Token which matches the close paren
				if (openToken.getType() == TokenClass.FUNCTION)
					pushOperator(rpn, openToken);
			}
		}
		while (!operatorStack.isEmpty()) {
			pushOperator(rpn, operatorStack.pop());
		}
		if (rpn.size() != 1) 
			System.err.println("Parse Error. Parse Tree does not include entire expression.");
		return rpn.pop();
	}
	
	private static void pushOperator(Stack<TreeNode> stack, Token operator) {
		TreeNode n = new TreeNode(operator);
		if (BINARY_OPS.contains(operator.getValue())) {
			n.children.add(stack.pop());
			n.children.add(stack.pop());
		} else if (operator.getType() == TokenClass.FUNCTION) {
			while (!stack.peek().t.equals(operator))
				n.children.add(stack.pop());
			stack.pop();
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
