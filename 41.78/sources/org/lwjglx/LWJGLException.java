package org.lwjglx;


public class LWJGLException extends Exception {
	private static final long serialVersionUID = 1L;

	public LWJGLException() {
	}

	public LWJGLException(String string) {
		super(string);
	}

	public LWJGLException(String string, Throwable throwable) {
		super(string, throwable);
	}

	public LWJGLException(Throwable throwable) {
		super(throwable);
	}
}
