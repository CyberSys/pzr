package org.lwjglx.opengl;

import org.lwjgl.opengl.GL30;


public final class Util {

	private Util() {
	}

	public static void checkGLError() throws OpenGLException {
		int int1 = GL30.glGetError();
		if (int1 != 0) {
			throw new OpenGLException(int1);
		}
	}

	public static String translateGLErrorString(int int1) {
		switch (int1) {
		case 0: 
			return "No error";
		
		case 1280: 
			return "Invalid enum";
		
		case 1281: 
			return "Invalid value";
		
		case 1282: 
			return "Invalid operation";
		
		case 1283: 
			return "Stack overflow";
		
		case 1284: 
			return "Stack underflow";
		
		case 1285: 
			return "Out of memory";
		
		case 1286: 
			return "Invalid framebuffer operation";
		
		case 32817: 
			return "Table too large";
		
		default: 
			return null;
		
		}
	}
}
