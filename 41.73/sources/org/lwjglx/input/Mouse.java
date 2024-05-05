package org.lwjglx.input;

import org.lwjgl.glfw.GLFW;
import org.lwjglx.LWJGLException;
import org.lwjglx.Sys;
import org.lwjglx.opengl.Display;


public class Mouse {
	private static boolean grabbed = false;
	private static int lastX = 0;
	private static int lastY = 0;
	private static int latestX = 0;
	private static int latestY = 0;
	private static int x = 0;
	private static int y = 0;
	private static EventQueue queue = new EventQueue(32);
	private static int[] buttonEvents;
	private static boolean[] buttonEventStates;
	private static int[] xEvents;
	private static int[] yEvents;
	private static int[] lastxEvents;
	private static int[] lastyEvents;
	private static long[] nanoTimeEvents;
	private static boolean clipPostionToDisplay;
	static double scrollxpos;
	static double scrollypos;

	public static void addMoveEvent(double double1, double double2) {
		latestX = (int)double1;
		latestY = Display.getHeight() - (int)double2;
		lastxEvents[queue.getNextPos()] = xEvents[queue.getNextPos()];
		lastyEvents[queue.getNextPos()] = yEvents[queue.getNextPos()];
		xEvents[queue.getNextPos()] = latestX;
		yEvents[queue.getNextPos()] = latestY;
		buttonEvents[queue.getNextPos()] = -1;
		buttonEventStates[queue.getNextPos()] = false;
		nanoTimeEvents[queue.getNextPos()] = Sys.getNanoTime();
		queue.add();
	}

	public static void addButtonEvent(int int1, boolean boolean1) {
		lastxEvents[queue.getNextPos()] = xEvents[queue.getNextPos()];
		lastyEvents[queue.getNextPos()] = yEvents[queue.getNextPos()];
		xEvents[queue.getNextPos()] = latestX;
		yEvents[queue.getNextPos()] = latestY;
		buttonEvents[queue.getNextPos()] = int1;
		buttonEventStates[queue.getNextPos()] = boolean1;
		nanoTimeEvents[queue.getNextPos()] = Sys.getNanoTime();
		queue.add();
	}

	public static void poll() {
		if (!grabbed) {
		}

		lastX = x;
		lastY = y;
		if (!grabbed && clipPostionToDisplay) {
			if (latestX < 0) {
				latestX = 0;
			}

			if (latestY < 0) {
				latestY = 0;
			}

			if (latestX > Display.getWidth() - 1) {
				latestX = Display.getWidth() - 1;
			}

			if (latestY > Display.getHeight() - 1) {
				latestY = Display.getHeight() - 1;
			}
		}

		x = latestX;
		y = latestY;
	}

	public static void create() throws LWJGLException {
	}

	public static boolean isCreated() {
		return Display.isCreated();
	}

	public static void setGrabbed(boolean boolean1) {
		GLFW.glfwSetInputMode(Display.getWindow(), 208897, boolean1 ? 212995 : 212993);
		grabbed = boolean1;
	}

	public static boolean isGrabbed() {
		return grabbed;
	}

	public static boolean isButtonDown(int int1) {
		return GLFW.glfwGetMouseButton(Display.getWindow(), int1) == 1;
	}

	public static boolean next() {
		return queue.next();
	}

	public static int getEventX() {
		return xEvents[queue.getCurrentPos()];
	}

	public static int getEventY() {
		return yEvents[queue.getCurrentPos()];
	}

	public static int getEventDX() {
		return xEvents[queue.getCurrentPos()] - lastxEvents[queue.getCurrentPos()];
	}

	public static int getEventDY() {
		return yEvents[queue.getCurrentPos()] - lastyEvents[queue.getCurrentPos()];
	}

	public static long getEventNanoseconds() {
		return nanoTimeEvents[queue.getCurrentPos()];
	}

	public static int getEventButton() {
		return buttonEvents[queue.getCurrentPos()];
	}

	public static boolean getEventButtonState() {
		return buttonEventStates[queue.getCurrentPos()];
	}

	public static int getEventDWheel() {
		return 0;
	}

	public static int getX() {
		return x;
	}

	public static int getY() {
		return y;
	}

	public static int getDX() {
		return x - lastX;
	}

	public static int getDY() {
		return y - lastY;
	}

	public static int getDWheel() {
		int int1 = (int)scrollypos;
		scrollypos = 0.0;
		return int1;
	}

	public static int getButtonCount() {
		return 8;
	}

	public static void setClipMouseCoordinatesToWindow(boolean boolean1) {
		clipPostionToDisplay = boolean1;
	}

	public static void setCursorPosition(int int1, int int2) {
		GLFW.glfwSetCursorPos(Display.getWindow(), (double)int1, (double)int2);
	}

	public static Cursor setNativeCursor(Cursor cursor) throws LWJGLException {
		GLFW.glfwSetCursor(Display.getWindow(), cursor.getHandle());
		return null;
	}

	public static void destroy() {
	}

	public static void updateCursor() {
	}

	public static void setDWheel(double double1, double double2) {
		scrollypos += double2;
		scrollxpos += double1;
	}

	static  {
		buttonEvents = new int[queue.getMaxEvents()];
		buttonEventStates = new boolean[queue.getMaxEvents()];
		xEvents = new int[queue.getMaxEvents()];
		yEvents = new int[queue.getMaxEvents()];
		lastxEvents = new int[queue.getMaxEvents()];
		lastyEvents = new int[queue.getMaxEvents()];
		nanoTimeEvents = new long[queue.getMaxEvents()];
		clipPostionToDisplay = true;
		scrollxpos = 0.0;
		scrollypos = 0.0;
	}
}
