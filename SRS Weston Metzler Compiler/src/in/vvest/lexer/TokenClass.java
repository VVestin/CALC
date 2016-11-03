package in.vvest.lexer;

public enum TokenClass {
	INT_VAR,			// A-Z and Θ
	FIXED_VAR,			// a-z
	STRING_VAR,			// Str0-Str9
	LIST_VAR,			// L1-L6
	MATRIX_VAR,			// [A]-[J]
	INT_LITERAL,		// 244
	FIXED_LITERAL,		// 6.13,
	STRING_LITERAL,		// "abc"
	OPEN_PAREN,			// (
	CLOSE_PAREN,		// )
	STO,				// ->
	COMMA,				// ,
	NEGATIVE,			// _
	COLON,				// :
	OPERATOR,			// +, -, *, /, and, or, >, =, etc.
	KEYWORD,			// Token with special meaning
	COMMAND,			// Does something. Returns no value
	FUNCTION,			// Takes arguments and returns a value
}
