package zombie.core.opengl;

import java.io.PrintStream;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.OpenGLException;
import org.lwjglx.opengl.Util;
import zombie.core.skinnedmodel.model.Model;


public class PZGLUtil {
	static int test = 0;

	public static void checkGLErrorThrow(String string, Object[] objectArray) throws OpenGLException {
		int int1 = GL11.glGetError();
		if (int1 != 0) {
			++test;
			throw new OpenGLException(createErrorMessage(int1, string, objectArray));
		}
	}

	private static String createErrorMessage(int int1, String string, Object[] objectArray) {
		String string2 = System.lineSeparator();
		return "  GL Error code (" + int1 + ") encountered." + string2 + "  Error translation: " + createErrorMessage(int1) + string2 + "  While performing: " + String.format(string, objectArray);
	}

	private static String createErrorMessage(int int1) {
		String string = Util.translateGLErrorString(int1);
		return string + " (" + int1 + ")";
	}

	public static boolean checkGLError(boolean boolean1) {
		try {
			Util.checkGLError();
			return true;
		} catch (OpenGLException openGLException) {
			RenderThread.logGLException(openGLException, boolean1);
			return false;
		}
	}

	public static void printGLState(PrintStream printStream) {
		int int1 = GL11.glGetInteger(2979);
		printStream.println("DEBUG: GL_MODELVIEW_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(2980);
		printStream.println("DEBUG: GL_PROJECTION_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(2981);
		printStream.println("DEBUG: GL_TEXTURE_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(2992);
		printStream.println("DEBUG: GL_ATTRIB_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(2993);
		printStream.println("DEBUG: GL_CLIENT_ATTRIB_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3381);
		printStream.println("DEBUG: GL_MAX_ATTRIB_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3382);
		printStream.println("DEBUG: GL_MAX_MODELVIEW_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3383);
		printStream.println("DEBUG: GL_MAX_NAME_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3384);
		printStream.println("DEBUG: GL_MAX_PROJECTION_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3385);
		printStream.println("DEBUG: GL_MAX_TEXTURE_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3387);
		printStream.println("DEBUG: GL_MAX_CLIENT_ATTRIB_STACK_DEPTH= " + int1);
		int1 = GL11.glGetInteger(3440);
		printStream.println("DEBUG: GL_NAME_STACK_DEPTH= " + int1);
	}

	public static void loadMatrix(Matrix4f matrix4f) {
		matrix4f.get(Model.m_staticReusableFloatBuffer);
		Model.m_staticReusableFloatBuffer.position(16);
		Model.m_staticReusableFloatBuffer.flip();
		GL11.glLoadMatrixf(Model.m_staticReusableFloatBuffer);
	}

	public static void multMatrix(Matrix4f matrix4f) {
		matrix4f.get(Model.m_staticReusableFloatBuffer);
		Model.m_staticReusableFloatBuffer.position(16);
		Model.m_staticReusableFloatBuffer.flip();
		GL11.glMultMatrixf(Model.m_staticReusableFloatBuffer);
	}

	public static void loadMatrix(int int1, Matrix4f matrix4f) {
		GL11.glMatrixMode(int1);
		loadMatrix(matrix4f);
	}

	public static void multMatrix(int int1, Matrix4f matrix4f) {
		GL11.glMatrixMode(int1);
		multMatrix(matrix4f);
	}

	public static void pushAndLoadMatrix(int int1, Matrix4f matrix4f) {
		GL11.glMatrixMode(int1);
		GL11.glPushMatrix();
		loadMatrix(matrix4f);
	}

	public static void pushAndMultMatrix(int int1, Matrix4f matrix4f) {
		GL11.glMatrixMode(int1);
		GL11.glPushMatrix();
		multMatrix(matrix4f);
	}

	public static void popMatrix(int int1) {
		GL11.glMatrixMode(int1);
		GL11.glPopMatrix();
	}
}
