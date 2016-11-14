package in.vvest.parser;

import java.util.ArrayList;
import java.util.List;

import in.vvest.lexer.Token;

public class TreeNode {
	protected Token t;
	protected List<TreeNode> children;
	
	public TreeNode(Token t) {
		this.t = t;
		children = new ArrayList<TreeNode>();
	}
	
	public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + "+--" + t);
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "   " : "|  "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).print(prefix + (isTail ?"   " : "|  "), true);
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
