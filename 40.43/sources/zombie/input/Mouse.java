package zombie.input;

import zombie.core.Core;


public class Mouse {
	protected static int x;
	protected static int y;
	public static boolean bLeftDown;
	public static boolean bLeftWasDown;
	public static boolean bRightDown;
	public static boolean bRightWasDown;
	public static boolean bMiddleDown;
	public static boolean bMiddleWasDown;
	public static long lastActivity;
	public static int wheelDelta;
	public static boolean[] UICaptured = new boolean[10];

	public static int getWheelState() {
		return wheelDelta;
	}

	public static synchronized int getXA() {
		return x;
	}

	public static synchronized int getYA() {
		return y;
	}

	public static synchronized int getX() {
		return (int)((float)x * Core.getInstance().getZoom(0));
	}

	public static synchronized int getY() {
		return (int)((float)y * Core.getInstance().getZoom(0));
	}

	public static boolean isButtonDown(int int1) {
		return org.lwjgl.input.Mouse.isCreated() ? org.lwjgl.input.Mouse.isButtonDown(int1) : false;
	}

	public static void UIBlockButtonDown(int int1) {
		UICaptured[int1] = true;
	}

	public static boolean isButtonDownUICheck(int int1) {
		if (org.lwjgl.input.Mouse.isCreated()) {
			boolean boolean1 = org.lwjgl.input.Mouse.isButtonDown(int1);
			if (!boolean1) {
				UICaptured[int1] = false;
			} else if (UICaptured[int1]) {
				return false;
			}

			return boolean1;
		} else {
			return false;
		}
	}

	public static boolean isLeftDown() {
		return bLeftDown;
	}

	public static boolean isLeftPressed() {
		return !bLeftWasDown && bLeftDown;
	}

	public static boolean isLeftReleased() {
		return bLeftWasDown && !bLeftDown;
	}

	public static boolean isLeftUp() {
		return !bLeftDown;
	}

	public static boolean isMiddleDown() {
		return bMiddleDown;
	}

	public static boolean isMiddlePressed() {
		return !bMiddleWasDown && bMiddleDown;
	}

	public static boolean isMiddleReleased() {
		return bMiddleWasDown && !bMiddleDown;
	}

	public static boolean isMiddleUp() {
		return !bMiddleDown;
	}

	public static boolean isRightDown() {
		return bRightDown;
	}

	public static boolean isRightPressed() {
		return !bRightWasDown && bRightDown;
	}

	public static boolean isRightReleased() {
		return bRightWasDown && !bRightDown;
	}

	public static boolean isRightUp() {
		return !bRightDown;
	}

	public static synchronized void update() {
		if (org.lwjgl.input.Mouse.isCreated()) {
			bLeftWasDown = bLeftDown;
			bRightWasDown = bRightDown;
			bMiddleWasDown = bMiddleDown;
			int int1 = x;
			int int2 = y;
			x = org.lwjgl.input.Mouse.getX();
			y = Core.getInstance().getScreenHeight() - org.lwjgl.input.Mouse.getY() - 1;
			bLeftDown = org.lwjgl.input.Mouse.isButtonDown(0);
			bRightDown = org.lwjgl.input.Mouse.isButtonDown(1);
			bMiddleDown = org.lwjgl.input.Mouse.isButtonDown(2);
			wheelDelta = org.lwjgl.input.Mouse.getDWheel();
			if (int1 != x || int2 != y || wheelDelta != 0 || bLeftWasDown != bLeftDown || bRightWasDown != bRightDown || bMiddleWasDown != bMiddleDown) {
				lastActivity = System.currentTimeMillis();
			}
		}
	}

	public static synchronized void setXY(int int1, int int2) {
		org.lwjgl.input.Mouse.setCursorPosition(int1, Core.getInstance().getOffscreenHeight(0) - 1 - int2);
	}
}
