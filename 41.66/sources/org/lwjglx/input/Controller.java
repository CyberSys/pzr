package org.lwjglx.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


public final class Controller {
	private final String joystickName;
	private final String gamepadName;
	private final int buttonsCount;
	private final int axisCount;
	private final int hatCount;
	private final int id;
	private final boolean isGamepad;
	private final String guid;
	private final float[] deadZone;
	public GamepadState gamepadState = null;
	private static final String[] axisNames = new String[]{"left stick X", "left stick Y", "right stick X", "right stick Y", "left trigger", "right trigger"};
	private static final String[] buttonNames = new String[]{"A", "B", "X", "Y", "left bumper", "right bumper", "back", "start", "guide", "left stick", "right stick", "d-pad up", "d-pad right", "d-pad down", "d-pad left"};

	public Controller(int int1) {
		this.id = int1;
		String string = GLFW.glfwGetJoystickName(int1);
		if (string == null) {
			string = "ControllerName" + int1;
		}

		this.joystickName = string;
		String string2 = GLFW.glfwGetGamepadName(int1);
		if (string2 == null) {
			string2 = "GamepadName" + int1;
		}

		this.gamepadName = string2;
		this.isGamepad = GLFW.glfwJoystickIsGamepad(int1);
		if (this.isGamepad) {
			this.axisCount = 6;
			this.buttonsCount = 15;
		} else {
			FloatBuffer floatBuffer = GLFW.glfwGetJoystickAxes(int1);
			this.axisCount = floatBuffer == null ? 0 : floatBuffer.remaining();
			ByteBuffer byteBuffer = GLFW.glfwGetJoystickButtons(int1);
			this.buttonsCount = byteBuffer == null ? 0 : byteBuffer.remaining();
		}

		ByteBuffer byteBuffer2 = GLFW.glfwGetJoystickHats(int1);
		this.hatCount = byteBuffer2 == null ? 0 : byteBuffer2.remaining();
		this.guid = GLFW.glfwGetJoystickGUID(int1);
		this.deadZone = new float[this.axisCount];
		Arrays.fill(this.deadZone, 0.2F);
	}

	public int getID() {
		return this.id;
	}

	public String getGUID() {
		return this.guid;
	}

	public boolean isGamepad() {
		return this.isGamepad;
	}

	public String getJoystickName() {
		return this.joystickName;
	}

	public String getGamepadName() {
		return this.gamepadName;
	}

	public int getAxisCount() {
		return this.axisCount;
	}

	public float getAxisValue(int int1) {
		if (this.gamepadState != null && this.gamepadState.bPolled) {
			return int1 >= 0 && int1 < 15 ? this.gamepadState.axesButtons.axes(int1) : 0.0F;
		} else {
			return 0.0F;
		}
	}

	public int getButtonCount() {
		return this.buttonsCount;
	}

	public int getHatCount() {
		return this.hatCount;
	}

	public int getHatState() {
		return this.gamepadState != null && this.gamepadState.bPolled ? this.gamepadState.hatState : 0;
	}

	public ByteBuffer getJoystickHats(int int1, ByteBuffer byteBuffer) {
		MemoryStack memoryStack = MemoryStack.stackGet();
		int int2 = memoryStack.getPointer();
		IntBuffer intBuffer = memoryStack.callocInt(1);
		ByteBuffer byteBuffer2;
		try {
			long long1 = GLFW.nglfwGetJoystickHats(int1, MemoryUtil.memAddress(intBuffer));
			byteBuffer.clear();
			byteBuffer.limit(intBuffer.get(0));
			if (long1 != 0L) {
				MemoryUtil.memCopy(long1, MemoryUtil.memAddress(byteBuffer), (long)intBuffer.get(0));
			}

			byteBuffer2 = byteBuffer;
		} finally {
			memoryStack.setPointer(int2);
		}

		return byteBuffer2;
	}

	public String getAxisName(int int1) {
		return axisNames[int1];
	}

	public float getXAxisValue() {
		return this.getAxisValue(0);
	}

	public float getYAxisValue() {
		return this.getAxisValue(1);
	}

	public float getDeadZone(int int1) {
		return this.deadZone[int1];
	}

	public void setDeadZone(int int1, float float1) {
		this.deadZone[int1] = float1;
	}

	public float getPovX() {
		if (this.gamepadState != null && this.gamepadState.bPolled) {
			if ((this.gamepadState.hatState & 8) != 0) {
				return -1.0F;
			} else {
				return (this.gamepadState.hatState & 2) != 0 ? 1.0F : 0.0F;
			}
		} else {
			return 0.0F;
		}
	}

	public float getPovY() {
		if (this.gamepadState != null && this.gamepadState.bPolled) {
			if ((this.gamepadState.hatState & 1) != 0) {
				return -1.0F;
			} else {
				return (this.gamepadState.hatState & 4) != 0 ? 1.0F : 0.0F;
			}
		} else {
			return 0.0F;
		}
	}

	public boolean isButtonPressed(int int1) {
		if (this.gamepadState != null && this.gamepadState.bPolled) {
			if (int1 >= 0 && int1 < 15) {
				return this.gamepadState.axesButtons.buttons(int1) == 1;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isButtonRelease(int int1) {
		if (this.gamepadState != null && this.gamepadState.bPolled) {
			if (int1 >= 0 && int1 < 15) {
				return this.gamepadState.axesButtons.buttons(int1) == 0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getButtonName(int int1) {
		if (int1 >= buttonNames.length) {
			int int2 = int1 - buttonNames.length;
			return "Extra button " + (int2 + 1);
		} else {
			return buttonNames[int1];
		}
	}

	public void poll(GamepadState gamepadState) {
		if (GLFW.glfwGetGamepadState(this.id, gamepadState.axesButtons)) {
			gamepadState.bPolled = true;
			ByteBuffer byteBuffer = this.getJoystickHats(this.id, gamepadState.hats);
			gamepadState.hatState = byteBuffer.remaining() == 0 ? 0 : byteBuffer.get(0);
		} else {
			gamepadState.bPolled = false;
		}
	}
}
