package org.lwjglx.opengl;

import java.awt.Canvas;
import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.LWJGLException;
import org.lwjglx.LWJGLUtil;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import zombie.core.Clipboard;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;


public class Display {
	private static String windowTitle = "Game";
	private static boolean displayCreated = false;
	private static boolean displayFocused = false;
	private static boolean displayVisible = true;
	private static boolean displayDirty = false;
	private static boolean displayResizable = true;
	private static boolean vsyncEnabled = true;
	private static DisplayMode gameWindowMode = new DisplayMode(640, 480);
	private static DisplayMode desktopDisplayMode = new DisplayMode(640, 480);
	private static int displayX = 0;
	private static int displayY = 0;
	private static boolean displayResized = false;
	private static int displayWidth = 0;
	private static int displayHeight = 0;
	private static int displayFramebufferWidth = 0;
	private static int displayFramebufferHeight = 0;
	private static Buffer displayIcons;
	private static long monitor;
	private static boolean isBorderlessWindow = false;
	private static boolean latestResized = false;
	private static int latestWidth = 0;
	private static int latestHeight = 0;
	public static GLCapabilities capabilities;
	private static final double[] mouseCursorPosX;
	private static final double[] mouseCursorPosY;
	private static int mouseCursorState;

	public static void create(PixelFormat pixelFormat) throws LWJGLException {
		GLFW.glfwWindowHint(135178, pixelFormat.getAccumulationBitsPerPixel());
		GLFW.glfwWindowHint(135172, pixelFormat.getAlphaBits());
		GLFW.glfwWindowHint(135179, pixelFormat.getAuxBuffers());
		GLFW.glfwWindowHint(135173, pixelFormat.getDepthBits());
		GLFW.glfwWindowHint(135181, pixelFormat.getSamples());
		GLFW.glfwWindowHint(135174, pixelFormat.getStencilBits());
		create();
	}

	public static void create() throws LWJGLException {
		if (Display.Window.handle != 0L) {
			GLFW.glfwDestroyWindow(Display.Window.handle);
		}

		GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(monitor);
		int int1 = gLFWVidMode.width();
		int int2 = gLFWVidMode.height();
		int int3 = gLFWVidMode.redBits() + gLFWVidMode.greenBits() + gLFWVidMode.blueBits();
		int int4 = gLFWVidMode.refreshRate();
		desktopDisplayMode = new DisplayMode(int1, int2, int3, int4);
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(139265, 196609);
		Display.Callbacks.errorCallback = GLFWErrorCallback.createPrint(System.err);
		GLFW.glfwSetErrorCallback(Display.Callbacks.errorCallback);
		GLFW.glfwWindowHint(131076, 0);
		GLFW.glfwWindowHint(131075, displayResizable ? 1 : 0);
		if (LWJGLUtil.getPlatform() == 2) {
			GLFW.glfwWindowHint(143361, 0);
		}

		boolean boolean1 = Core.bDebug && "true".equalsIgnoreCase(System.getProperty("org.lwjgl.util.Debug"));
		GLFW.glfwWindowHint(139271, boolean1 ? 1 : 0);
		if (Core.getInstance().getOptionBorderlessWindow()) {
			isBorderlessWindow = true;
			GLFW.glfwWindowHint(131077, 0);
			Display.Window.handle = GLFW.glfwCreateWindow(gameWindowMode.getWidth(), gameWindowMode.getHeight(), windowTitle, 0L, 0L);
		} else if (isFullscreen()) {
			Display.Window.handle = GLFW.glfwCreateWindow(gameWindowMode.getWidth(), gameWindowMode.getHeight(), windowTitle, monitor, 0L);
		} else {
			Display.Window.handle = GLFW.glfwCreateWindow(gameWindowMode.getWidth(), gameWindowMode.getHeight(), windowTitle, 0L, 0L);
		}

		if (Display.Window.handle == 0L) {
			throw new IllegalStateException("Failed to create Display window");
		} else {
			GLFW.glfwSetWindowIcon(Display.Window.handle, displayIcons);
			Display.Callbacks.bNoise = boolean1;
			Display.Callbacks.initCallbacks();
			calcWindowPos(isBorderlessWindow() || isFullscreen());
			GLFW.glfwSetWindowPos(Display.Window.handle, displayX, displayY);
			GLFW.glfwShowWindow(Display.Window.handle);
			GLFW.glfwMakeContextCurrent(Display.Window.handle);
			capabilities = GL.createCapabilities();
			GLFW.glfwSwapInterval(0);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glClear(16640);
			GLFW.glfwSwapBuffers(Display.Window.handle);
			setVSyncEnabled(vsyncEnabled);
			int[] intArray;
			if (boolean1 && capabilities.OpenGL43) {
				intArray = new int[]{131185};
				GL43.glDebugMessageControl(33350, 33361, 4352, intArray, false);
			}

			intArray = new int[1];
			int[] intArray2 = new int[1];
			GLFW.glfwGetWindowSize(Display.Window.handle, intArray, intArray2);
			displayWidth = latestWidth = intArray[0];
			displayHeight = latestHeight = intArray2[0];
			displayCreated = true;
		}
	}

	public static boolean isCreated() {
		return displayCreated;
	}

	public static boolean isActive() {
		return displayFocused;
	}

	public static boolean isVisible() {
		return displayVisible;
	}

	public static void setLocation(int int1, int int2) {
		System.out.println("TODO: Implement Display.setLocation(int, int)");
	}

	public static void setVSyncEnabled(boolean boolean1) {
		vsyncEnabled = boolean1;
		if (boolean1) {
			GLFW.glfwSwapInterval(1);
		} else {
			GLFW.glfwSwapInterval(0);
		}
	}

	public static long getWindow() {
		return Display.Window.handle;
	}

	public static void update() {
		update(true);
	}

	public static void update(boolean boolean1) {
		try {
			swapBuffers();
			displayDirty = false;
		} catch (LWJGLException lWJGLException) {
			throw new RuntimeException(lWJGLException);
		}

		if (boolean1) {
			processMessages();
		}
	}

	private static void updateMouseCursor() {
		int int1 = RenderThread.isCursorVisible() ? 212993 : 212994;
		boolean boolean1 = Core.getInstance().getOptionLockCursorToWindow();
		if (boolean1) {
			int1 = 212995;
		}

		if (mouseCursorState != int1) {
			boolean boolean2 = mouseCursorState == 212995;
			if (boolean2) {
				GLFW.glfwGetCursorPos(getWindow(), mouseCursorPosX, mouseCursorPosY);
			}

			mouseCursorState = int1;
			GLFW.glfwSetInputMode(getWindow(), 208897, int1);
			if (boolean2) {
				GLFW.glfwSetCursorPos(getWindow(), mouseCursorPosX[0], mouseCursorPosY[0]);
			}
		}

		if (boolean1) {
			GLFW.glfwGetCursorPos(getWindow(), mouseCursorPosX, mouseCursorPosY);
			int int2 = (int)mouseCursorPosX[0];
			int int3 = (int)mouseCursorPosY[0];
			mouseCursorPosX[0] = (double)PZMath.clamp((int)mouseCursorPosX[0], 0, getWidth());
			mouseCursorPosY[0] = (double)PZMath.clamp((int)mouseCursorPosY[0], 0, getHeight());
			if (int2 != (int)mouseCursorPosX[0] || int3 != (int)mouseCursorPosY[0]) {
				GLFW.glfwSetCursorPos(getWindow(), mouseCursorPosX[0], mouseCursorPosY[0]);
			}
		}
	}

	public static void processMessages() {
		GLFW.glfwPollEvents();
		Keyboard.poll();
		Mouse.poll();
		updateMouseCursor();
		if (latestResized) {
			latestResized = false;
			displayResized = true;
			displayWidth = latestWidth;
			displayHeight = latestHeight;
		} else {
			displayResized = false;
		}
	}

	public static void swapBuffers() throws LWJGLException {
		GLFW.glfwSwapBuffers(Display.Window.handle);
	}

	public static void destroy() {
		Display.Callbacks.releaseCallbacks();
		GLFW.glfwDestroyWindow(Display.Window.handle);
		displayCreated = false;
	}

	public static void setDisplayModeAndFullscreen(DisplayMode displayMode) throws LWJGLException {
		setDisplayModeAndFullscreenInternal(displayMode, displayMode.isFullscreenCapable());
	}

	public static void setFullscreen(boolean boolean1) {
		setDisplayModeAndFullscreenInternal(gameWindowMode, boolean1);
	}

	public static boolean isFullscreen() {
		if (!isCreated()) {
			return Core.getInstance().isFullScreen();
		} else {
			return GLFW.glfwGetWindowMonitor(Display.Window.handle) != 0L;
		}
	}

	public static void setBorderlessWindow(boolean boolean1) {
		isBorderlessWindow = boolean1;
		if (isCreated()) {
			GLFW.glfwSetWindowAttrib(getWindow(), 131077, boolean1 ? 0 : 1);
		}
	}

	public static boolean isBorderlessWindow() {
		return isBorderlessWindow;
	}

	public static void setDisplayMode(DisplayMode displayMode) throws LWJGLException {
		if (displayMode == null) {
			throw new NullPointerException();
		} else {
			setDisplayModeAndFullscreenInternal(displayMode, displayMode.isFullscreenCapable() && isFullscreen());
		}
	}

	private static void setDisplayModeAndFullscreenInternal(DisplayMode displayMode, boolean boolean1) {
		boolean boolean2 = isFullscreen();
		DisplayMode displayMode2 = gameWindowMode;
		gameWindowMode = displayMode;
		Core.setFullScreen(boolean1);
		if (isCreated() && (boolean2 != boolean1 || !gameWindowMode.equals(displayMode2))) {
			GLFW.glfwHideWindow(Display.Window.handle);
			calcWindowPos(boolean1 || isBorderlessWindow());
			GLFW.glfwSetWindowMonitor(Display.Window.handle, boolean1 ? monitor : 0L, displayX, displayY, gameWindowMode.getWidth(), gameWindowMode.getHeight(), -1);
			GLFW.glfwSetWindowIcon(Display.Window.handle, displayIcons);
			GLFW.glfwShowWindow(Display.Window.handle);
			GLFW.glfwFocusWindow(Display.Window.handle);
			GLFW.glfwMakeContextCurrent(Display.Window.handle);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			GLFW.glfwSwapInterval(0);
			GL11.glClear(16640);
			GLFW.glfwSwapBuffers(Display.Window.handle);
			setVSyncEnabled(vsyncEnabled);
		}
	}

	private static void calcWindowPos(boolean boolean1) {
		MemoryStack memoryStack = MemoryStack.stackPush();
		try {
			IntBuffer intBuffer = memoryStack.callocInt(1);
			IntBuffer intBuffer2 = memoryStack.callocInt(1);
			GLFW.glfwGetFramebufferSize(Display.Window.handle, intBuffer, intBuffer2);
			displayFramebufferWidth = intBuffer.get(0);
			displayFramebufferHeight = intBuffer2.get(0);
			IntBuffer intBuffer3 = memoryStack.callocInt(1);
			IntBuffer intBuffer4 = memoryStack.callocInt(1);
			GLFW.glfwGetWindowFrameSize(Display.Window.handle, intBuffer3, intBuffer4, (IntBuffer)null, (IntBuffer)null);
			int int1 = intBuffer3.get(0);
			int int2 = intBuffer4.get(0);
			displayWidth = gameWindowMode.getWidth();
			displayHeight = gameWindowMode.getHeight();
			if (boolean1) {
				int1 = 0;
				int2 = 0;
			}

			displayX = int1 + (desktopDisplayMode.getWidth() - gameWindowMode.getWidth()) / 2;
			displayY = int2 + (desktopDisplayMode.getHeight() - gameWindowMode.getHeight()) / 2;
			if (gameWindowMode.getWidth() > desktopDisplayMode.getWidth()) {
				displayX = int1;
			}

			if (gameWindowMode.getHeight() > desktopDisplayMode.getHeight()) {
				displayY = int2;
			}
		} catch (Throwable throwable) {
			if (memoryStack != null) {
				try {
					memoryStack.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}
			}

			throw throwable;
		}

		if (memoryStack != null) {
			memoryStack.close();
		}
	}

	public static DisplayMode getDisplayMode() {
		return gameWindowMode;
	}

	public static DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
		org.lwjgl.glfw.GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(GLFW.glfwGetPrimaryMonitor());
		DisplayMode[] displayModeArray = new DisplayMode[buffer.capacity()];
		for (int int1 = 0; int1 < displayModeArray.length; ++int1) {
			buffer.position(int1);
			int int2 = buffer.width();
			int int3 = buffer.height();
			int int4 = buffer.redBits() + buffer.greenBits() + buffer.blueBits();
			int int5 = buffer.refreshRate();
			displayModeArray[int1] = new DisplayMode(int2, int3, int4, int5);
		}

		return displayModeArray;
	}

	public static DisplayMode getDesktopDisplayMode() {
		return desktopDisplayMode;
	}

	public static boolean wasResized() {
		return displayResized;
	}

	public static int getX() {
		return displayX;
	}

	public static int getY() {
		return displayY;
	}

	public static int getWidth() {
		return latestWidth;
	}

	public static int getHeight() {
		return latestHeight;
	}

	public static int getFramebufferWidth() {
		return displayFramebufferWidth;
	}

	public static int getFramebufferHeight() {
		return displayFramebufferHeight;
	}

	public static void setTitle(String string) {
		windowTitle = string;
		if (isCreated()) {
			GLFW.glfwSetWindowTitle(Display.Window.handle, windowTitle);
		}
	}

	public static boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(Display.Window.handle);
	}

	public static boolean isDirty() {
		return displayDirty;
	}

	public static void setInitialBackground(float float1, float float2, float float3) {
		System.out.println("TODO: Implement Display.setInitialBackground(float, float, float)");
	}

	public static void setIcon(Buffer buffer) {
		displayIcons = buffer;
	}

	public static void setResizable(boolean boolean1) {
		displayResizable = boolean1;
	}

	public static boolean isResizable() {
		return displayResizable;
	}

	public static void setParent(Canvas canvas) throws LWJGLException {
	}

	public static void releaseContext() throws LWJGLException {
		GLFW.glfwMakeContextCurrent(0L);
	}

	public static boolean isCurrent() throws LWJGLException {
		return GLFW.glfwGetCurrentContext() == Display.Window.handle;
	}

	public static void makeCurrent() throws LWJGLException {
		GLFW.glfwMakeContextCurrent(Display.Window.handle);
		GL.setCapabilities(capabilities);
	}

	public static String getAdapter() {
		return "GeNotSupportedAdapter";
	}

	public static String getVersion() {
		return "1.0 NOT SUPPORTED";
	}

	public static void sync(int int1) {
		Sync.sync(int1);
	}

	static  {
	if (!GLFW.glfwInit()) {
		throw new IllegalStateException("Unable to initialize GLFW");
	} else {
		GLFW.glfwInitHint(327681, 0);
		Keyboard.create();
		monitor = GLFW.glfwGetPrimaryMonitor();
		GLFWVidMode var0 = GLFW.glfwGetVideoMode(monitor);
		int var1 = var0.width();
		int var2 = var0.height();
		int var3 = var0.redBits() + var0.greenBits() + var0.blueBits();
		int var4 = var0.refreshRate();
		desktopDisplayMode = new DisplayMode(var1, var2, var3, var4);
		mouseCursorPosX = new double[1];
		mouseCursorPosY = new double[1];
		mouseCursorState = -1;
	}
	}

	private static final class Window {
		static long handle;
	}

	private static final class Callbacks {
		static boolean bNoise = false;
		static GLFWErrorCallback errorCallback;
		static GLDebugMessageCallback debugMessageCallback;
		static GLFWKeyCallback keyCallback;
		static GLFWCharCallback charCallback;
		static GLFWCursorPosCallback cursorPosCallback;
		static GLFWMouseButtonCallback mouseButtonCallback;
		static GLFWScrollCallback scrollCallback;
		static GLFWWindowFocusCallback windowFocusCallback;
		static GLFWWindowIconifyCallback windowIconifyCallback;
		static GLFWWindowSizeCallback windowSizeCallback;
		static GLFWWindowPosCallback windowPosCallback;
		static GLFWWindowRefreshCallback windowRefreshCallback;
		static GLFWFramebufferSizeCallback framebufferSizeCallback;

		static void initCallbacks() {
			cursorPosCallback = GLFWCursorPosCallback.create((var0,var2,var4)->{
				Mouse.addMoveEvent(var2, var4);
			});
			GLFW.glfwSetCursorPosCallback(Display.getWindow(), cursorPosCallback);
			mouseButtonCallback = GLFWMouseButtonCallback.create((var0,var2,var3,var4)->{
				Mouse.addButtonEvent(var2, var3 == 1);
			});
			GLFW.glfwSetMouseButtonCallback(Display.getWindow(), mouseButtonCallback);
			windowFocusCallback = GLFWWindowFocusCallback.create((var0,var2)->{
				if (bNoise) {
					DebugLog.log("glfwSetWindowFocusCallback focused=" + var2);
				}

				Display.displayFocused = var2;
				if (var2) {
					Clipboard.rememberCurrentValue();
				}
			});
			GLFW.glfwSetWindowFocusCallback(Display.getWindow(), windowFocusCallback);
			windowIconifyCallback = GLFWWindowIconifyCallback.create((var0,var2)->{
				if (bNoise) {
					DebugLog.log("glfwSetWindowIconifyCallback iconifed=" + var2);
				}

				Display.displayVisible = !var2;
			});
			GLFW.glfwSetWindowIconifyCallback(Display.getWindow(), windowIconifyCallback);
			windowSizeCallback = GLFWWindowSizeCallback.create((var0,var2,var3)->{
				if (bNoise) {
					DebugLog.log("glfwSetWindowSizeCallback width,height=" + var2 + "," + var3);
				}

				if (var2 + var3 != 0) {
					Display.latestResized = true;
					Display.latestWidth = var2;
					Display.latestHeight = var3;
				}
			});
			GLFW.glfwSetWindowSizeCallback(Display.getWindow(), windowSizeCallback);
			scrollCallback = GLFWScrollCallback.create((var0,var2,var4)->{
				Mouse.setDWheel(var2, var4);
			});
			GLFW.glfwSetScrollCallback(Display.getWindow(), scrollCallback);
			windowPosCallback = GLFWWindowPosCallback.create((var0,var2,var3)->{
				if (bNoise) {
					DebugLog.log("glfwSetWindowPosCallback x,y=" + var2 + "," + var3);
				}

				Display.displayX = var2;
				Display.displayY = var3;
			});
			GLFW.glfwSetWindowPosCallback(Display.getWindow(), windowPosCallback);
			windowRefreshCallback = GLFWWindowRefreshCallback.create((var0)->{
				Display.displayDirty = true;
			});
			GLFW.glfwSetWindowRefreshCallback(Display.getWindow(), windowRefreshCallback);
			framebufferSizeCallback = GLFWFramebufferSizeCallback.create((var0,var2,var3)->{
				if (bNoise) {
					DebugLog.log("glfwSetFramebufferSizeCallback width,height=" + var2 + "," + var3);
				}

				Display.displayFramebufferWidth = var2;
				Display.displayFramebufferHeight = var3;
			});
			GLFW.glfwSetFramebufferSizeCallback(Display.getWindow(), framebufferSizeCallback);
			keyCallback = GLFWKeyCallback.create((var0,var2,var3,var4,var5)->{
				Keyboard.addKeyEvent(var2, var4);
			});
			GLFW.glfwSetKeyCallback(Display.getWindow(), keyCallback);
			charCallback = GLFWCharCallback.create((var0,var2)->{
				Keyboard.addCharEvent((char)var2);
			});
			GLFW.glfwSetCharCallback(Display.getWindow(), charCallback);
		}

		static void releaseCallbacks() {
			errorCallback.free();
			if (debugMessageCallback != null) {
				debugMessageCallback.free();
			}

			keyCallback.free();
			charCallback.free();
			cursorPosCallback.free();
			mouseButtonCallback.free();
			scrollCallback.free();
			windowFocusCallback.free();
			windowIconifyCallback.free();
			windowSizeCallback.free();
			windowPosCallback.free();
			windowRefreshCallback.free();
			framebufferSizeCallback.free();
		}
	}
}
