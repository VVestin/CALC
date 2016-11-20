package in.vvest.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.vvest.lexer.Token;

public class TreeNode {
	protected Token t;
	protected List<TreeNode> children;
	
	public TreeNode(Token t) {
		this.t = t;
		children = new ArrayList<TreeNode>();
	}
	
	public Token getToken() {
		return t;
	}

	public void setToken(Token t) {
		this.t = t;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public void print() {
        print("", true);
    }
	
	public void reverseChildren() {
		Collections.reverse(children);
		for (int i = 0; i < children.size(); i++)
			children.get(i).reverseChildren();
	}

    private void print(String prefix, boolean isTail) {
    	if (t == null) {
    		for (int i = 0; i < children.size(); i++)
    			children.get(i).print();
    	} else {
	        System.out.println(prefix + "+--" + t);
	        for (int i = 0; i < children.size() - 1; i++) {
	            children.get(i).print(prefix + (isTail ? "   " : "|  "), false);
	        }
	        if (children.size() > 0) {
	            children.get(children.size() - 1).print(prefix + (isTail ?"   " : "|  "), true);
	        }
    	}
    }
	
	public String toString() {
		String s = "";
		s += t;
		for (TreeNode child : children)
			s += child;
		return s+ " ";
	}
}
