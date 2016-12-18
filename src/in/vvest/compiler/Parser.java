package in.vvest.compiler;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;

public class Parser {
	private Stack<Token> operatorStack;
	private Stack<Token> rpn;
	private Iterator<Token> it;
	private Set<Token> markedTokens;

	public Token parse(List<Token> src) {
		operatorStack = new Stack<Token>();
		rpn = new Stack<Token>();
		it = src.iterator();
		markedTokens = new HashSet<Token>();
		boolean newLine = false;
		boolean endLine = false;
		while (it.hasNext()) {
			Token next = it.next();
			System.out.println("rpn " + rpn);
			System.out.println("operatorStack " + operatorStack + "\n");
			System.out.println("Looking at " + next);
			if (next instanceof Colon) {
				newLine = true;
				endLine = false;
				operatorStack.push(next);
				continue;
			} else if (endLine) { 
				System.err.println("Parse Error. Instructions must be on their own lines");
			} else if (next instanceof Operator.Store) {
				while (!(operatorStack.peek() instanceof Colon))
					pushOperator(operatorStack.pop());
				operatorStack.pop();
				if (!it.hasNext())
					System.err.println("Parse Error. Assignment operator has no right side.");
				pushOperator(it.next());
				pushOperator(next);
				endLine = true;
			} else if (next instanceof Identifier || next instanceof Literal) {
				rpn.push(next);
			} else if (next instanceof Operator) {
				while (!operatorStack.isEmpty() && !((Operator) next).isHigherPriority(operatorStack.peek())) {
					pushOperator(operatorStack.pop());
				}
				operatorStack.push(next);
			} else if (next instanceof Parenthesis && ((Parenthesis) next).isOpen()) {
				operatorStack.push(next);
			} else if (next instanceof Function || next instanceof ControlStructure && !(next instanceof ControlStructure.End)) {
				rpn.push(next);
				markedTokens.add(next);
				operatorStack.push(next);
			} else if (next instanceof Parenthesis) { // Found a close paren
				while (!(operatorStack.peek() instanceof Parenthesis
							|| operatorStack.peek() instanceof Function
							|| operatorStack.peek() instanceof ControlStructure)) {
					pushOperator(operatorStack.pop());
							}
				Token openToken = operatorStack.pop(); // Token which matches the close paren
				if (openToken instanceof Function) {
					pushOperator(openToken);
				} else if (openToken instanceof ControlStructure) {
					operatorStack.push(openToken);
					while (!(markedTokens.contains(rpn.peek()))) {
						openToken.getChildren().add(rpn.pop());
					}
				}
			} else if (next instanceof ControlStructure.End) {
				while (!(operatorStack.peek() instanceof ControlStructure)) {
					pushOperator(operatorStack.pop());
				}
				pushOperator(operatorStack.pop());
				endLine = true;
			} else if (next instanceof Command) {
				if (!newLine) {
					System.err.println("Parse Error. Cannot execute " + next + " inline");
				} else {
					pushOperator(next);
					endLine = true;
				}
			} else if (next instanceof Comma) {
				while (!(operatorStack.peek() instanceof Function || operatorStack.peek() instanceof ControlStructure)) {
					pushOperator(operatorStack.pop());
				}
				operatorStack.peek().getChildren().add(rpn.pop());
			}
			newLine = false;
		}
		while (!operatorStack.isEmpty()) {
			pushOperator(operatorStack.pop());
		}
		Token prgm = new Block();
		while (!rpn.isEmpty()) {
			prgm.getChildren().add(rpn.pop());
		}
		prgm.reverseChildren();
		return prgm;
	}

	private void pushOperator(Token operator) {
		if (operator instanceof Operator.Negative) {
			operator.getChildren().add(rpn.pop());
		} else if (operator instanceof Operator) {
			operator.getChildren().add(rpn.pop());
			operator.getChildren().add(rpn.pop());
		} else if (operator instanceof Function) {
			while (!(markedTokens.contains(rpn.peek()))) {
				operator.getChildren().add(rpn.pop());
			}
			markedTokens.remove(rpn.peek());
			rpn.pop();
		} else if (operator instanceof ControlStructure) {
			Block block = new Block();
			while (!markedTokens.contains(rpn.peek()))
				block.getChildren().add(rpn.pop());
			operator.getChildren().add(block);
			markedTokens.remove(rpn.peek());
			rpn.pop();
		} else if (operator instanceof Colon) {
			return;
		}
		rpn.push(operator);
	}
}
