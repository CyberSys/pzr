package zombie.core.input;

import java.util.ArrayList;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.input.ControllerState;
import zombie.input.ControllerStateCache;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;


public final class Input {
	public static final int ANY_CONTROLLER = -1;
	private final Controller[] controllers = new Controller[16];
	private final ArrayList newlyConnected = new ArrayList();
	private final ArrayList newlyDisconnected = new ArrayList();
	private final boolean[][] controllerPressed = new boolean[16][15];
	private final boolean[][] controllerWasPressed = new boolean[16][15];
	private final float[][] controllerPov = new float[16][2];
	private final ControllerStateCache m_controllerStateCache = new ControllerStateCache();

	public static String getKeyName(int int1) {
		String string = Keyboard.getKeyName(int1);
		if ("LSHIFT".equals(string)) {
			string = "Left SHIFT";
		}

		if ("RSHIFT".equals(string)) {
			string = "Right SHIFT";
		}

		if ("LMENU".equals(string)) {
			string = "Left ALT";
		} else if ("RMENU".equals(string)) {
			string = "Right ALT";
		}

		return string;
	}

	public static int getKeyCode(String string) {
		if ("Right SHIFT".equals(string)) {
			return 54;
		} else if ("Left SHIFT".equals(string)) {
			return 42;
		} else if ("Left ALT".equals(string)) {
			return 56;
		} else {
			return "Right ALT".equals(string) ? 184 : Keyboard.getKeyIndex(string);
		}
	}

	public int getControllerCount() {
		return this.controllers.length;
	}

	public int getAxisCount(int int1) {
		Controller controller = this.getController(int1);
		return controller == null ? 0 : controller.getAxisCount();
	}

	public float getAxisValue(int int1, int int2) {
		Controller controller = this.getController(int1);
		return controller == null ? 0.0F : controller.getAxisValue(int2);
	}

	public String getAxisName(int int1, int int2) {
		Controller controller = this.getController(int1);
		return controller == null ? null : controller.getAxisName(int2);
	}

	public boolean isControllerLeftD(int int1) {
		if (int1 == -1) {
			for (int int2 = 0; int2 < this.controllers.length; ++int2) {
				if (this.isControllerLeftD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			Controller controller = this.getController(int1);
			if (controller == null) {
				return false;
			} else {
				return controller.getPovX() < -0.5F;
			}
		}
	}

	public boolean isControllerRightD(int int1) {
		if (int1 == -1) {
			for (int int2 = 0; int2 < this.controllers.length; ++int2) {
				if (this.isControllerRightD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			Controller controller = this.getController(int1);
			if (controller == null) {
				return false;
			} else {
				return controller.getPovX() > 0.5F;
			}
		}
	}

	public boolean isControllerUpD(int int1) {
		if (int1 == -1) {
			for (int int2 = 0; int2 < this.controllers.length; ++int2) {
				if (this.isControllerUpD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			Controller controller = this.getController(int1);
			if (controller == null) {
				return false;
			} else {
				return controller.getPovY() < -0.5F;
			}
		}
	}

	public boolean isControllerDownD(int int1) {
		if (int1 == -1) {
			for (int int2 = 0; int2 < this.controllers.length; ++int2) {
				if (this.isControllerDownD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			Controller controller = this.getController(int1);
			if (controller == null) {
				return false;
			} else {
				return controller.getPovY() > 0.5F;
			}
		}
	}

	private Controller checkControllerButton(int int1, int int2) {
		Controller controller = this.getController(int1);
		if (controller == null) {
			return null;
		} else {
			return int2 >= 0 && int2 < controller.getButtonCount() ? controller : null;
		}
	}

	public boolean isButtonPressedD(int int1, int int2) {
		if (int2 == -1) {
			for (int int3 = 0; int3 < this.controllers.length; ++int3) {
				if (this.isButtonPressedD(int1, int3)) {
					return true;
				}
			}

			return false;
		} else {
			Controller controller = this.checkControllerButton(int2, int1);
			return controller == null ? false : this.controllerPressed[int2][int1];
		}
	}

	public boolean wasButtonPressed(int int1, int int2) {
		Controller controller = this.checkControllerButton(int1, int2);
		return controller == null ? false : this.controllerWasPressed[int1][int2];
	}

	public boolean isButtonStartPress(int int1, int int2) {
		return !this.wasButtonPressed(int1, int2) && this.isButtonPressedD(int2, int1);
	}

	public boolean isButtonReleasePress(int int1, int int2) {
		return this.wasButtonPressed(int1, int2) && !this.isButtonPressedD(int2, int1);
	}

	public void initControllers() {
		this.updateGameThread();
		this.updateGameThread();
	}

	private void onControllerConnected(Controller controller) {
		JoypadManager.instance.onControllerConnected(controller);
		if (LuaManager.env != null) {
			LuaEventManager.triggerEvent("OnGamepadConnect", controller.getID());
		}
	}

	private void onControllerDisconnected(Controller controller) {
		JoypadManager.instance.onControllerDisconnected(controller);
		if (LuaManager.env != null) {
			LuaEventManager.triggerEvent("OnGamepadDisconnect", controller.getID());
		}
	}

	public void poll() {
		if (!Core.getInstance().isDoingTextEntry()) {
			while (true) {
				if (GameKeyboard.getEventQueuePolling().next()) {
					continue;
				}
			}
		}

		while (Mouse.next()) {
		}

		this.m_controllerStateCache.poll();
	}

	public Controller getController(int int1) {
		return int1 >= 0 && int1 < this.controllers.length ? this.controllers[int1] : null;
	}

	public int getButtonCount(int int1) {
		Controller controller = this.getController(int1);
		return controller == null ? null : controller.getButtonCount();
	}

	public String getButtonName(int int1, int int2) {
		Controller controller = this.getController(int1);
		return controller == null ? null : controller.getButtonName(int2);
	}

	public void updateGameThread() {
		if (!this.m_controllerStateCache.getState().isCreated()) {
			this.m_controllerStateCache.swap();
		} else {
			ControllerState controllerState = this.m_controllerStateCache.getState();
			int int1;
			Controller controller;
			if (this.checkConnectDisconnect(controllerState)) {
				for (int1 = 0; int1 < this.newlyDisconnected.size(); ++int1) {
					controller = (Controller)this.newlyDisconnected.get(int1);
					this.onControllerDisconnected(controller);
				}

				for (int1 = 0; int1 < this.newlyConnected.size(); ++int1) {
					controller = (Controller)this.newlyConnected.get(int1);
					this.onControllerConnected(controller);
				}
			}

			for (int1 = 0; int1 < this.getControllerCount(); ++int1) {
				controller = this.getController(int1);
				if (controller != null) {
					int int2 = controller.getButtonCount();
					int int3;
					for (int3 = 0; int3 < int2; ++int3) {
						this.controllerWasPressed[int1][int3] = this.controllerPressed[int1][int3];
						if (this.controllerPressed[int1][int3] && !controller.isButtonPressed(int3)) {
							this.controllerPressed[int1][int3] = false;
						} else if (!this.controllerPressed[int1][int3] && controller.isButtonPressed(int3)) {
							this.controllerPressed[int1][int3] = true;
							JoypadManager.instance.onPressed(int1, int3);
						}
					}

					int2 = controller.getAxisCount();
					float float1;
					for (int3 = 0; int3 < int2; ++int3) {
						float1 = controller.getAxisValue(int3);
						if ((!controller.isGamepad() || int3 != 4) && int3 != 5) {
							if (float1 < -0.5F) {
								JoypadManager.instance.onPressedAxisNeg(int1, int3);
							}

							if (float1 > 0.5F) {
								JoypadManager.instance.onPressedAxis(int1, int3);
							}
						} else if (float1 > 0.0F) {
							JoypadManager.instance.onPressedTrigger(int1, int3);
						}
					}

					float float2 = controller.getPovX();
					float1 = controller.getPovY();
					if (float2 != this.controllerPov[int1][0] || float1 != this.controllerPov[int1][1]) {
						this.controllerPov[int1][0] = float2;
						this.controllerPov[int1][1] = float1;
						JoypadManager.instance.onPressedPov(int1);
					}
				}
			}

			this.m_controllerStateCache.swap();
		}
	}

	private boolean checkConnectDisconnect(ControllerState controllerState) {
		boolean boolean1 = false;
		this.newlyConnected.clear();
		this.newlyDisconnected.clear();
		for (int int1 = 0; int1 < 16; ++int1) {
			Controller controller = controllerState.getController(int1);
			if (controller != this.controllers[int1]) {
				boolean1 = true;
				if (controller != null && controller.isGamepad()) {
					this.newlyConnected.add(controller);
				} else {
					if (this.controllers[int1] != null) {
						this.newlyDisconnected.add(this.controllers[int1]);
					}

					controller = null;
				}

				this.controllers[int1] = controller;
			}
		}

		return boolean1;
	}

	public void quit() {
		this.m_controllerStateCache.quit();
	}
}
