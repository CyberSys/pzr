package org.luaj.kahluafork.compiler;


public class Token {
	int token;
	double r;
	String ts;

	public void set(Token token) {
		this.token = token.token;
		this.r = token.r;
		this.ts = token.ts;
	}
}
