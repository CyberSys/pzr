package zombie.input;

import org.lwjglx.input.Controller;
import org.lwjglx.input.Controllers;
import org.lwjglx.input.GamepadState;


public class ControllerState {
	private boolean m_isCreated = false;
	private boolean m_wasPolled = false;
	private final Controller[] m_controllers = new Controller[16];
	private final GamepadState[] m_gamepadState = new GamepadState[16];

	ControllerState() {
		for (int int1 = 0; int1 < this.m_controllers.length; ++int1) {
			this.m_gamepadState[int1] = new GamepadState();
		}
	}

	public void poll() {
		boolean boolean1 = !this.m_isCreated;
		this.m_isCreated = this.m_isCreated || Controllers.isCreated();
		if (this.m_isCreated) {
			if (boolean1) {
			}

			this.m_wasPolled = true;
			Controllers.poll(this.m_gamepadState);
			for (int int1 = 0; int1 < Controllers.getControllerCount(); ++int1) {
				this.m_controllers[int1] = Controllers.getController(int1);
			}
		}
	}

	public boolean wasPolled() {
		return this.m_wasPolled;
	}

	public void set(ControllerState controllerState) {
		this.m_isCreated = controllerState.m_isCreated;
		for (int int1 = 0; int1 < this.m_controllers.length; ++int1) {
			this.m_controllers[int1] = controllerState.m_controllers[int1];
			if (this.m_controllers[int1] != null) {
				this.m_gamepadState[int1].set(controllerState.m_gamepadState[int1]);
				this.m_controllers[int1].gamepadState = this.m_gamepadState[int1];
			}
		}

		this.m_wasPolled = controllerState.m_wasPolled;
	}

	public void reset() {
		this.m_wasPolled = false;
	}

	public boolean isCreated() {
		return this.m_isCreated;
	}

	public Controller getController(int int1) {
		return this.m_controllers[int1];
	}

	public void quit() {
		for (int int1 = 0; int1 < this.m_controllers.length; ++int1) {
			this.m_gamepadState[int1].quit();
		}
	}
}
