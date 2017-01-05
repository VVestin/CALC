package in.vvest.compiler;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;

public class Parser {
	private Stack<Token> operatorStack;
	private Stack<Token> rpn;
	private ListIterator<Token> it;
	private List<Token> markedTokens;
	private Map<String, ControlStructure.FunDef> funTable;
	private List<Function.FunCall> funCalls;

	public Token parse(List<Token> src) {
		operatorStack = new Stack<Token>();
		operatorStack.push(new Colon());
		rpn = new Stack<Token>();
		it = src.listIterator();
		markedTokens = new LinkedList<Token>();
		funTable = new HashMap<String, ControlStructure.FunDef>();
		funCalls = new LinkedList<Function.FunCall>();
		boolean newLine = false;
		boolean endLine = false;
		Token last = null;
		while (it.hasNext()) {
			Token next = it.next();
			System.out.println("next " + next);
			if (next instanceof Colon) {
				newLine = true;
				endLine = false;
				while (!(operatorStack.peek() instanceof Colon)) {
					pushOperator(operatorStack.pop());
				}
				continue;
			} else if (endLine) { 
				System.err.println("Parse Error. Instructions must be on their own lines");
			} else if (next instanceof Identifier || next instanceof Literal) {
				rpn.push(next);
			} else if (next instanceof Operator) {
				while (!operatorStack.isEmpty() && !((Operator) next).isHigherPriority(operatorStack.peek())) {
					pushOperator(operatorStack.pop());
				}
				operatorStack.push(next);
			} else if (next instanceof ControlStructure.FunDef) {
				Token id = it.next();
				String fun = ""; 
				while (!(id instanceof Parenthesis)) {
					fun += ((Identifier) id).getIdentifier();
					id = it.next();
				}
				((ControlStructure.FunDef) next).setID(fun);
				rpn.push(next);
				markedTokens.add(next);
				operatorStack.push(next);
				funTable.put(fun, (ControlStructure.FunDef) next);
			} else if (next instanceof Parenthesis && ((Parenthesis) next).isOpen()) {
				if (last != null & last instanceof Identifier && ((Identifier) last).getType() == Type.INTEGER) {
					String fun = "";
					while (!rpn.isEmpty() && rpn.peek() instanceof Identifier && ((Identifier) rpn.peek()).getType() == Type.INTEGER) {
						fun = ((Identifier) rpn.pop()).getIdentifier() + fun;
					}
					Function.FunCall funCall = new Function.FunCall(fun);
					operatorStack.push(funCall);
					markedTokens.add(funCall);
					rpn.push(funCall);
					funCalls.add(funCall);
				} else {
					operatorStack.push(next);
				}
			} else if (next instanceof ListBrace && ((ListBrace) next).isOpen()) {
				operatorStack.push(next);
				rpn.push(next);
				markedTokens.add(next);
			} else if (next instanceof ListBracket && ((ListBracket) next).isOpen()) {
				operatorStack.push(next);
			} else if (next instanceof ListBracket) {
				while (!(operatorStack.peek() instanceof ListBracket))
					pushOperator(operatorStack.pop());
				operatorStack.pop();
				ListAccess access = new ListAccess();
				access.setIndex(rpn.pop());
				access.setList(rpn.pop());
				rpn.push(access);
			} else if (next instanceof ListBrace) { // Must be a close ListBrace
				while (!(operatorStack.peek() instanceof ListBrace))
					pushOperator(operatorStack.pop());
				pushOperator(operatorStack.pop());
			} else if (next instanceof ControlStructure.End || next instanceof ControlStructure.Else || next instanceof ControlStructure.ElseIf) {
				while (!(operatorStack.peek() instanceof ControlStructure)) {
					pushOperator(operatorStack.pop());
				}
				endLine = true;
				if (next instanceof ControlStructure.End) {
					pushOperator(operatorStack.pop());
				} else {
					pushOperator(next);
					if (next instanceof ControlStructure.ElseIf) {
						operatorStack.push(next);
						markedTokens.add(next);
						endLine = false;
					}
				}
				operatorStack.push(new Colon());
			} else if (next instanceof Function || next instanceof ControlStructure) {
				rpn.push(next);
				markedTokens.add(next);
				operatorStack.push(next);
			} else if (next instanceof Parenthesis) { // Must be a close paren
				while (!(operatorStack.peek() instanceof Parenthesis
							|| operatorStack.peek() instanceof Function
							|| operatorStack.peek() instanceof ControlStructure)) 
					pushOperator(operatorStack.pop());
				Token openToken = operatorStack.pop(); // Token which matches the close paren
				if (openToken instanceof Function) {
					pushOperator(openToken);
				} else if (openToken instanceof ControlStructure) {
					if (!(openToken instanceof ControlStructure.ElseIf))
						operatorStack.push(openToken);
					while (!(markedTokens.contains(rpn.peek()))) {
						openToken.getChildren().add(rpn.pop());
					}
					operatorStack.push(new Colon());
				}
			} else if (next instanceof Command) {
				if (!newLine) {
					System.err.println("Parse Error. Cannot execute " + next + " inline");
				} else {
					pushOperator(next);
					endLine = true;
				}
			} else if (next instanceof Comma) {
				while (!(operatorStack.peek() instanceof Function || operatorStack.peek() instanceof ControlStructure || operatorStack.peek() instanceof ListBrace)) {
					pushOperator(operatorStack.pop());
				}
				operatorStack.peek().getChildren().add(rpn.pop());
			}
			last = next;
			newLine = false;
		}
		while (!operatorStack.isEmpty()) {
			pushOperator(operatorStack.pop());
		}
		for (Function.FunCall fun : funCalls) {
			if (!funTable.keySet().contains(fun.getID())) System.err.println("Parse Error. Function " + fun.getID() + " has not been defined");
			fun.setFun(funTable.get(fun.getID()));
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
		} else if (operator instanceof ListBrace) {
			Literal list = new Literal(Type.LIST, "");
			while (!markedTokens.contains(rpn.peek())) {
				list.getChildren().add(rpn.pop());
			}
			markedTokens.remove(rpn.peek());
			Token openBrace = rpn.pop();
			while (!openBrace.getChildren().isEmpty()) {
				list.getChildren().add(openBrace.getChildren().remove(openBrace.getChildren().size() - 1));
			}
			rpn.push(list);
			return;
		} else if (operator instanceof Function) {
			while (!markedTokens.contains(rpn.peek())) {
				operator.getChildren().add(rpn.pop());
			}
			markedTokens.remove(rpn.peek());
			rpn.pop();
		} else if (operator instanceof ControlStructure.Else || operator instanceof ControlStructure.ElseIf) {
			System.out.println("DEBUG " + operator);
			Block block = new Block();
			while (!markedTokens.contains(rpn.peek())) {
				block.getChildren().add(rpn.pop());
			}	
			rpn.peek().getChildren().add(block);
			rpn.peek().getChildren().add(operator);
			return;
		} else if (operator instanceof ControlStructure) {
			Block block = new Block();
			while (!markedTokens.contains(rpn.peek())) {
				block.getChildren().add(rpn.pop());
			}	
			operator.getChildren().add(block);
			markedTokens.remove(rpn.peek());
			rpn.pop();
		} else if (operator instanceof Colon) {
			return;
		}
		rpn.push(operator);
	}
}
