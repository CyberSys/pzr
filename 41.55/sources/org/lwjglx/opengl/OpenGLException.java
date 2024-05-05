package org.lwjglx.opengl;


public class OpenGLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OpenGLException(int int1) {
		this(createErrorMessage(int1));
	}

	private static String createErrorMessage(int int1) {
		String string = Util.translateGLErrorString(int1);
		return string + " (" + int1 + ")";
	}

	public OpenGLException() {
	}

	public OpenGLException(String string) {
		super(string);
	}

	public OpenGLException(String string, Throwable throwable) {
		super(string, throwable);
	}

	public OpenGLException(Throwable throwable) {
		super(throwable);
	}
}
