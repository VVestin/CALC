package in.vvest.generator;

import in.vvest.parser.TreeNode;

// 
public class Generator {

	public static String generate(TreeNode parseTree) {
		StringBuilder out = new StringBuilder();
		out.append("SETUP");
		for (TreeNode t : parseTree.getChildren()) {
			
		}
		return out.toString();
	}
	
	private static void generateSub(TreeNode sub, StringBuilder out) {
		
	}
	
}
