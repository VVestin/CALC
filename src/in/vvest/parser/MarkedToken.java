package in.vvest.parser;

import in.vvest.lexer.Token;
import in.vvest.lexer.TokenClass;

class MarkerToken extends Token {
	public MarkerToken(TokenClass type, String value) {
		super(type, value);
	}
	
	public String toString() {
		return "*" + super.toString();
	}
}