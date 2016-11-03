package in.vvest.lexer;

public class Token {

	private TokenClass type;
	private String value;
	
	public Token(TokenClass type, String value) {
		this.type = type;
		this.value = value;
	}

	public TokenClass getType() {
		return type;
	}

	public void setType(TokenClass type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
