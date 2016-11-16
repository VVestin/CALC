package in.vvest.lexer;

public enum TI84Token {
	ABS_VAL(TokenClass.FUNCTION, "abs("),
	AND(TokenClass.OPERATOR, " and "),
	CLEAR_HOME(TokenClass.COMMAND, "ClrHome"),
	COSINE(TokenClass.FUNCTION, "cos("),
	DELETE_VAR(TokenClass.COMMAND, "DelVar "),
	DIM(TokenClass.FUNCTION, "dim("),
	DISP(TokenClass.COMMAND, "Disp "),
	ELSE(TokenClass.KEYWORD, "Else"),
	ELSEIF(TokenClass.KEYWORD, "ElseIf "),
	END(TokenClass.KEYWORD, "End"),
	FILL(TokenClass.FUNCTION, "Fill("),
	FIXED(TokenClass.FUNCTION, "Fixed("),
	FOR(TokenClass.KEYWORD, "For("),
	F_PART(TokenClass.FUNCTION, "fPart("),
	GET_KEY(TokenClass.FUNCTION, "getKey"),
	GET_TIME(TokenClass.FUNCTION, "getTime"),
	GOTO(TokenClass.KEYWORD, "Goto "),
	IF(TokenClass.KEYWORD, "If "),
	INPUT(TokenClass.COMMAND, "input "),
	IN_STRING(TokenClass.FUNCTION, "inString("),
	INT(TokenClass.FUNCTION, "int("),
	I_PART(TokenClass.FUNCTION, "iPart("),
	LABEL(TokenClass.KEYWORD, "Lbl "),
	LENGTH(TokenClass.FUNCTION, "length("),
	LOG_BASE(TokenClass.FUNCTION, "logBASE("),
	MAX(TokenClass.FUNCTION, "max("),
	MEAN(TokenClass.FUNCTION, "mean("),
	MEDIAN(TokenClass.FUNCTION, "median("),
	MENU(TokenClass.COMMAND, "Menu("),
	MIN(TokenClass.FUNCTION, "min("),
	NOT(TokenClass.FUNCTION, "not("),
	OR(TokenClass.OPERATOR, " or "),
	OUTPUT(TokenClass.COMMAND, "Output("),
	PAUSE(TokenClass.COMMAND, "Pause"),
	RAND(TokenClass.FUNCTION, "rand"),
	RAND_INT(TokenClass.FUNCTION, "randInt("),
	REPEAT(TokenClass.KEYWORD, "Repeat "),
	ROUND(TokenClass.FUNCTION, "round("),
	SET_TIME(TokenClass.COMMAND, "setTime("),
	SINE(TokenClass.FUNCTION, "sin("),
	SORT_ASCENDING(TokenClass.COMMAND, "SortA("),
	SORT_DESCENDING(TokenClass.COMMAND, "SortD("),
	STOP(TokenClass.COMMAND, "Stop"),
	SUBSTRING(TokenClass.FUNCTION, "sub("),
	SUM(TokenClass.FUNCTION, "sum("),
	TAN(TokenClass.FUNCTION, "tan("),
	THEN(TokenClass.KEYWORD, "Then"),
	WHILE(TokenClass.KEYWORD, "While "),
	XOR(TokenClass.OPERATOR, " xor "),
	STO(TokenClass.OPERATOR, "->");
	
	private Token token;
	private String text;
	
	TI84Token(TokenClass type, String text) {
		String tokenValue = text.trim().toLowerCase();
		if (tokenValue.endsWith("(")) 
			tokenValue = tokenValue.substring(0, tokenValue.length() - 1);
		token = new Token(type, tokenValue);
		this.text = text;
	}

	public Token getToken() {
		return token;
	}

	public String getText() {
		return text;
	}
	
}
