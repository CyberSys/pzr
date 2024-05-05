package zombie.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;


public final class Clipboard {
	private static Thread MainThread = null;
	private static String PreviousKnownValue = null;
	private static String DelaySetMainThread = null;

	public static void initMainThread() {
		MainThread = Thread.currentThread();
		PreviousKnownValue = getClipboard();
	}

	public static void rememberCurrentValue() {
		if (Thread.currentThread() == MainThread) {
			GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null);
			try {
				PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
			} catch (Throwable throwable) {
				PreviousKnownValue = "";
			} finally {
				GLFW.glfwSetErrorCallback(gLFWErrorCallback);
			}
		}
	}

	public static synchronized String getClipboard() {
		if (Thread.currentThread() == MainThread) {
			GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null);
			String string;
			try {
				String string2 = PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
				return string2;
			} catch (Throwable throwable) {
				PreviousKnownValue = "";
				string = "";
			} finally {
				GLFW.glfwSetErrorCallback(gLFWErrorCallback);
			}

			return string;
		} else {
			return PreviousKnownValue;
		}
	}

	public static synchronized void setClipboard(String string) {
		PreviousKnownValue = string;
		if (Thread.currentThread() == MainThread) {
			GLFW.glfwSetClipboardString(0L, string);
		} else {
			DelaySetMainThread = string;
		}
	}

	public static synchronized void updateMainThread() {
		if (DelaySetMainThread != null) {
			setClipboard(DelaySetMainThread);
			DelaySetMainThread = null;
		}
	}
}
