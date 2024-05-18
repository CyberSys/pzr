package zombie.core.input;

import java.io.IOException;
import java.util.ArrayDeque;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Rumbler;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;


public class XInputController extends AbstractController {
	final short dwUserIndex;
	private final XINPUT_STATE stateLast = new XINPUT_STATE();
	private final XINPUT_STATE stateCur = new XINPUT_STATE();
	private long lastPoll;
	public static final int XINPUT_GAMEPAD_DPAD_UP = 1;
	public static final int XINPUT_GAMEPAD_DPAD_DOWN = 2;
	public static final int XINPUT_GAMEPAD_DPAD_LEFT = 4;
	public static final int XINPUT_GAMEPAD_DPAD_RIGHT = 8;
	public static final int XINPUT_GAMEPAD_START = 16;
	public static final int XINPUT_GAMEPAD_BACK = 32;
	public static final int XINPUT_GAMEPAD_LEFT_THUMB = 64;
	public static final int XINPUT_GAMEPAD_RIGHT_THUMB = 128;
	public static final int XINPUT_GAMEPAD_LEFT_SHOULDER = 256;
	public static final int XINPUT_GAMEPAD_RIGHT_SHOULDER = 512;
	public static final int XINPUT_GAMEPAD_A = 4096;
	public static final int XINPUT_GAMEPAD_B = 8192;
	public static final int XINPUT_GAMEPAD_X = 16384;
	public static final int XINPUT_GAMEPAD_Y = 32768;
	public static final int DPAD_ALL = 15;
	public static final int[] ComponentButtonBits = new int[]{4096, 8192, 16384, 32768, 256, 512, 32, 16, 64, 128, 0, 0, 0, 0, 0, 0};
	private final ArrayDeque events = new ArrayDeque();

	public static XInputController create(int int1, String string) {
		Component[] componentArray = new Component[16];
		byte byte1 = 0;
		int int2 = byte1 + 1;
		componentArray[byte1] = new XInputComponent("A", Button.A);
		componentArray[int2++] = new XInputComponent("B", Button.B);
		componentArray[int2++] = new XInputComponent("X", Button.X);
		componentArray[int2++] = new XInputComponent("Y", Button.Y);
		componentArray[int2++] = new XInputComponent("LeftShoulder", Button.LEFT_THUMB2);
		componentArray[int2++] = new XInputComponent("RightShoulder", Button.RIGHT_THUMB2);
		componentArray[int2++] = new XInputComponent("Back", Button.BACK);
		componentArray[int2++] = new XInputComponent("Start", Button.SELECT);
		componentArray[int2++] = new XInputComponent("LeftThumb", Button.LEFT_THUMB);
		componentArray[int2++] = new XInputComponent("RightThumb", Button.RIGHT_THUMB);
		componentArray[int2++] = new XInputComponent("LeftY", Axis.Y);
		componentArray[int2++] = new XInputComponent("LeftX", Axis.X);
		componentArray[int2++] = new XInputComponent("RightY", Axis.RY);
		componentArray[int2++] = new XInputComponent("RightX", Axis.RX);
		componentArray[int2++] = new XInputComponent("Trigger", Axis.Z);
		componentArray[int2++] = new XInputComponent("POV", Axis.POV);
		return new XInputController(int1, string, componentArray, new Controller[0], new Rumbler[0]);
	}

	protected XInputController(int int1, String string, Component[] componentArray, Controller[] controllerArray, Rumbler[] rumblerArray) {
		super(string, componentArray, controllerArray, rumblerArray);
		this.dwUserIndex = (short)int1;
	}

	protected void pollDevice() throws IOException {
		if (!this.stateCur.bConnected) {
			long long1 = System.currentTimeMillis();
			if (long1 - this.lastPoll < 5000L) {
				return;
			}

			this.lastPoll = long1;
		}

		if (this.stateCur.nGetState(this.dwUserIndex)) {
			float float1;
			if (this.stateLast.wButtons != this.stateCur.wButtons) {
				boolean boolean1;
				if ((this.stateLast.wButtons & 15) != (this.stateCur.wButtons & 15)) {
					float1 = 0.0F;
					boolean boolean2 = (this.stateCur.wButtons & 1) != 0;
					boolean boolean3 = (this.stateCur.wButtons & 2) != 0;
					boolean1 = (this.stateCur.wButtons & 4) != 0;
					boolean boolean4 = (this.stateCur.wButtons & 8) != 0;
					if (boolean2 && !boolean1 && !boolean4) {
						float1 = 0.25F;
					} else if (boolean2 && boolean4) {
						float1 = 0.375F;
					} else if (boolean4 && !boolean3) {
						float1 = 0.5F;
					} else if (boolean4 && boolean3) {
						float1 = 0.625F;
					} else if (boolean3 && !boolean1) {
						float1 = 0.75F;
					} else if (boolean3 && boolean1) {
						float1 = 0.875F;
					} else if (boolean1 && !boolean2) {
						float1 = 1.0F;
					} else if (boolean1 && boolean2) {
						float1 = 0.125F;
					}

					XInputController.XInputEvent xInputEvent = XInputController.XInputEvent.alloc();
					xInputEvent.set(this.getComponent(Axis.POV), float1);
					this.events.addLast(xInputEvent);
				}

				Component[] componentArray = this.getComponents();
				for (int int1 = 0; int1 < componentArray.length; ++int1) {
					int int2 = ComponentButtonBits[int1];
					if (int2 != 0) {
						boolean1 = (this.stateCur.wButtons & int2) != 0;
						if ((this.stateLast.wButtons & int2) != (this.stateCur.wButtons & int2)) {
							XInputController.XInputEvent xInputEvent2 = XInputController.XInputEvent.alloc();
							xInputEvent2.set(componentArray[int1], boolean1 ? 1.0F : 0.0F);
							this.events.addLast(xInputEvent2);
						}
					}
				}

				this.stateLast.wButtons = this.stateCur.wButtons;
			}

			XInputController.XInputEvent xInputEvent3;
			if (this.stateLast.bLeftTrigger != this.stateCur.bLeftTrigger || this.stateLast.bRightTrigger != this.stateCur.bRightTrigger) {
				float1 = (float)(this.stateCur.bLeftTrigger - this.stateCur.bRightTrigger) / 255.0F;
				xInputEvent3 = XInputController.XInputEvent.alloc();
				xInputEvent3.set(this.getComponent(Axis.Z), float1);
				this.events.addLast(xInputEvent3);
				this.stateLast.bLeftTrigger = this.stateCur.bLeftTrigger;
				this.stateLast.bRightTrigger = this.stateCur.bRightTrigger;
			}

			if (this.stateLast.sThumbLX != this.stateCur.sThumbLX) {
				float1 = this.stateCur.sThumbLX > 0 ? (float)this.stateCur.sThumbLX / 32767.0F : (float)this.stateCur.sThumbLX / 32768.0F;
				xInputEvent3 = XInputController.XInputEvent.alloc();
				xInputEvent3.set(this.getComponent(Axis.X), float1);
				this.events.addLast(xInputEvent3);
				this.stateLast.sThumbLX = this.stateCur.sThumbLX;
			}

			if (this.stateLast.sThumbLY != this.stateCur.sThumbLY) {
				float1 = this.stateCur.sThumbLY > 0 ? (float)this.stateCur.sThumbLY / 32767.0F : (float)this.stateCur.sThumbLY / 32768.0F;
				float1 *= -1.0F;
				xInputEvent3 = XInputController.XInputEvent.alloc();
				xInputEvent3.set(this.getComponent(Axis.Y), float1);
				this.events.addLast(xInputEvent3);
				this.stateLast.sThumbLY = this.stateCur.sThumbLY;
			}

			if (this.stateLast.sThumbRX != this.stateCur.sThumbRX) {
				float1 = this.stateCur.sThumbRX > 0 ? (float)this.stateCur.sThumbRX / 32767.0F : (float)this.stateCur.sThumbRX / 32768.0F;
				xInputEvent3 = XInputController.XInputEvent.alloc();
				xInputEvent3.set(this.getComponent(Axis.RX), float1);
				this.events.addLast(xInputEvent3);
				this.stateLast.sThumbRX = this.stateCur.sThumbRX;
			}

			if (this.stateLast.sThumbRY != this.stateCur.sThumbRY) {
				float1 = this.stateCur.sThumbRY > 0 ? (float)this.stateCur.sThumbRY / 32767.0F : (float)this.stateCur.sThumbRY / 32768.0F;
				float1 *= -1.0F;
				xInputEvent3 = XInputController.XInputEvent.alloc();
				xInputEvent3.set(this.getComponent(Axis.RY), float1);
				this.events.addLast(xInputEvent3);
				this.stateLast.sThumbRY = this.stateCur.sThumbRY;
			}
		}
	}

	protected boolean getNextDeviceEvent(Event event) throws IOException {
		if (this.events.isEmpty()) {
			return false;
		} else {
			XInputController.XInputEvent xInputEvent = (XInputController.XInputEvent)this.events.removeFirst();
			event.set(xInputEvent.component, xInputEvent.value, xInputEvent.ns);
			XInputController.XInputEvent.release(xInputEvent);
			return true;
		}
	}

	public boolean isConnected() {
		return this.stateCur.bConnected;
	}

	private static final class XInputEvent {
		Component component;
		float value;
		long ns;
		private static final ArrayDeque freeEvents = new ArrayDeque();

		void set(Component component, float float1) {
			this.component = component;
			this.value = float1;
			this.ns = System.nanoTime();
			((XInputComponent)component).pollData = float1;
		}

		static XInputController.XInputEvent alloc() {
			return freeEvents.isEmpty() ? new XInputController.XInputEvent() : (XInputController.XInputEvent)freeEvents.pop();
		}

		static void release(XInputController.XInputEvent xInputEvent) {
			freeEvents.push(xInputEvent);
		}
	}
}
