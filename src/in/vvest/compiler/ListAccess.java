package in.vvest.compiler;

import java.util.List;

public class ListAccess extends Identifier { 
	private Token list; 
	private Token index;

	public ListAccess() {
		super(Type.LIST, "access");
	}

	public void compile(List<String> code) {
		list.compile(code);
		index.compile(code);
		code.add("call ListGet");
	}

	public String getAddress() {
		return ((Identifier) list).getAddress();
	}

	public void setList(Token list) {
		this.list = list;
	}

	public void setIndex(Token index) {
		this.index = index;
	}

	public Token getList() {
		return list;
	}

	public Token getIndex() {
		return index;
	}

	public Type getType() {
		return Type.INTEGER;
	}

	public boolean isCompileable() {
		return true;
	}
}
