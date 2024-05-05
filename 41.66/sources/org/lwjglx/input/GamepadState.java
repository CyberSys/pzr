package org.lwjglx.input;

import java.nio.ByteBuffer;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;


public final class GamepadState {
	public boolean bPolled = false;
	public final GLFWGamepadState axesButtons = GLFWGamepadState.malloc();
	public final ByteBuffer hats = MemoryUtil.memAlloc(8);
	public int hatState = 0;

	public void set(GamepadState gamepadState) {
		this.bPolled = gamepadState.bPolled;
		this.axesButtons.set(gamepadState.axesButtons);
		this.hats.clear();
		gamepadState.hats.position(0);
		this.hats.put(gamepadState.hats);
		this.hatState = gamepadState.hatState;
	}

	public void quit() {
		this.axesButtons.free();
		MemoryUtil.memFree(this.hats);
	}
}
