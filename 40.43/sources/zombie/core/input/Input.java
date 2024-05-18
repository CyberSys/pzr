package zombie.core.input;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller.Type;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import zombie.input.JoypadManager;


public class Input {
	public static final int ANY_CONTROLLER = -1;
	public static final int MAX_CONTROLLERS = 100;
	private static final int MAX_BUTTONS = 100;
	public static final int KEY_ESCAPE = 1;
	public static final int KEY_1 = 2;
	public static final int KEY_2 = 3;
	public static final int KEY_3 = 4;
	public static final int KEY_4 = 5;
	public static final int KEY_5 = 6;
	public static final int KEY_6 = 7;
	public static final int KEY_7 = 8;
	public static final int KEY_8 = 9;
	public static final int KEY_9 = 10;
	public static final int KEY_0 = 11;
	public static final int KEY_MINUS = 12;
	public static final int KEY_EQUALS = 13;
	public static final int KEY_BACK = 14;
	public static final int KEY_TAB = 15;
	public static final int KEY_Q = 16;
	public static final int KEY_W = 17;
	public static final int KEY_E = 18;
	public static final int KEY_R = 19;
	public static final int KEY_T = 20;
	public static final int KEY_Y = 21;
	public static final int KEY_U = 22;
	public static final int KEY_I = 23;
	public static final int KEY_O = 24;
	public static final int KEY_P = 25;
	public static final int KEY_LBRACKET = 26;
	public static final int KEY_RBRACKET = 27;
	public static final int KEY_RETURN = 28;
	public static final int KEY_ENTER = 28;
	public static final int KEY_LCONTROL = 29;
	public static final int KEY_A = 30;
	public static final int KEY_S = 31;
	public static final int KEY_D = 32;
	public static final int KEY_F = 33;
	public static final int KEY_G = 34;
	public static final int KEY_H = 35;
	public static final int KEY_J = 36;
	public static final int KEY_K = 37;
	public static final int KEY_L = 38;
	public static final int KEY_SEMICOLON = 39;
	public static final int KEY_APOSTROPHE = 40;
	public static final int KEY_GRAVE = 41;
	public static final int KEY_LSHIFT = 42;
	public static final int KEY_BACKSLASH = 43;
	public static final int KEY_Z = 44;
	public static final int KEY_X = 45;
	public static final int KEY_C = 46;
	public static final int KEY_V = 47;
	public static final int KEY_B = 48;
	public static final int KEY_N = 49;
	public static final int KEY_M = 50;
	public static final int KEY_COMMA = 51;
	public static final int KEY_PERIOD = 52;
	public static final int KEY_SLASH = 53;
	public static final int KEY_RSHIFT = 54;
	public static final int KEY_MULTIPLY = 55;
	public static final int KEY_LMENU = 56;
	public static final int KEY_SPACE = 57;
	public static final int KEY_CAPITAL = 58;
	public static final int KEY_F1 = 59;
	public static final int KEY_F2 = 60;
	public static final int KEY_F3 = 61;
	public static final int KEY_F4 = 62;
	public static final int KEY_F5 = 63;
	public static final int KEY_F6 = 64;
	public static final int KEY_F7 = 65;
	public static final int KEY_F8 = 66;
	public static final int KEY_F9 = 67;
	public static final int KEY_F10 = 68;
	public static final int KEY_NUMLOCK = 69;
	public static final int KEY_SCROLL = 70;
	public static final int KEY_NUMPAD7 = 71;
	public static final int KEY_NUMPAD8 = 72;
	public static final int KEY_NUMPAD9 = 73;
	public static final int KEY_SUBTRACT = 74;
	public static final int KEY_NUMPAD4 = 75;
	public static final int KEY_NUMPAD5 = 76;
	public static final int KEY_NUMPAD6 = 77;
	public static final int KEY_ADD = 78;
	public static final int KEY_NUMPAD1 = 79;
	public static final int KEY_NUMPAD2 = 80;
	public static final int KEY_NUMPAD3 = 81;
	public static final int KEY_NUMPAD0 = 82;
	public static final int KEY_DECIMAL = 83;
	public static final int KEY_F11 = 87;
	public static final int KEY_F12 = 88;
	public static final int KEY_F13 = 100;
	public static final int KEY_F14 = 101;
	public static final int KEY_F15 = 102;
	public static final int KEY_KANA = 112;
	public static final int KEY_CONVERT = 121;
	public static final int KEY_NOCONVERT = 123;
	public static final int KEY_YEN = 125;
	public static final int KEY_NUMPADEQUALS = 141;
	public static final int KEY_CIRCUMFLEX = 144;
	public static final int KEY_AT = 145;
	public static final int KEY_COLON = 146;
	public static final int KEY_UNDERLINE = 147;
	public static final int KEY_KANJI = 148;
	public static final int KEY_STOP = 149;
	public static final int KEY_AX = 150;
	public static final int KEY_UNLABELED = 151;
	public static final int KEY_NUMPADENTER = 156;
	public static final int KEY_RCONTROL = 157;
	public static final int KEY_NUMPADCOMMA = 179;
	public static final int KEY_DIVIDE = 181;
	public static final int KEY_SYSRQ = 183;
	public static final int KEY_RMENU = 184;
	public static final int KEY_PAUSE = 197;
	public static final int KEY_HOME = 199;
	public static final int KEY_UP = 200;
	public static final int KEY_PRIOR = 201;
	public static final int KEY_LEFT = 203;
	public static final int KEY_RIGHT = 205;
	public static final int KEY_END = 207;
	public static final int KEY_DOWN = 208;
	public static final int KEY_NEXT = 209;
	public static final int KEY_INSERT = 210;
	public static final int KEY_DELETE = 211;
	public static final int KEY_LWIN = 219;
	public static final int KEY_RWIN = 220;
	public static final int KEY_APPS = 221;
	public static final int KEY_POWER = 222;
	public static final int KEY_SLEEP = 223;
	public static final int KEY_LALT = 56;
	public static final int KEY_RALT = 184;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;
	private static final int BUTTON1 = 4;
	private static final int BUTTON2 = 5;
	private static final int BUTTON3 = 6;
	private static final int BUTTON4 = 7;
	private static final int BUTTON5 = 8;
	private static final int BUTTON6 = 9;
	private static final int BUTTON7 = 10;
	private static final int BUTTON8 = 11;
	private static final int BUTTON9 = 12;
	private static final int BUTTON10 = 13;
	public static final int MOUSE_LEFT_BUTTON = 0;
	public static final int MOUSE_RIGHT_BUTTON = 1;
	public static final int MOUSE_MIDDLE_BUTTON = 2;
	private static boolean controllersInited = false;
	private static ArrayList controllers = new ArrayList();
	private int lastMouseX;
	private int lastMouseY;
	protected boolean[] mousePressed = new boolean[10];
	private boolean[][] controllerPressed = new boolean[100][100];
	private float[][] controllerPov = new float[100][2];
	protected char[] keys = new char[1024];
	protected boolean[] pressed = new boolean[1024];
	protected long[] nextRepeat = new long[1024];
	private boolean[][] controls = new boolean[100][110];
	protected boolean consumed = false;
	protected HashSet allListeners = new HashSet();
	protected ArrayList keyListeners = new ArrayList();
	protected ArrayList keyListenersToAdd = new ArrayList();
	protected ArrayList mouseListeners = new ArrayList();
	protected ArrayList mouseListenersToAdd = new ArrayList();
	protected ArrayList controllerListeners = new ArrayList();
	private int wheel;
	private int height;
	private boolean displayActive = true;
	private boolean keyRepeat;
	private int keyRepeatInitial;
	private int keyRepeatInterval;
	private boolean paused;
	private float scaleX = 1.0F;
	private float scaleY = 1.0F;
	private float xoffset = 0.0F;
	private float yoffset = 0.0F;
	private int doubleClickDelay = 250;
	private long doubleClickTimeout = 0L;
	private int clickX;
	private int clickY;
	private int clickButton;
	private int pressedX = -1;
	private int pressedY = -1;
	private int mouseClickTolerance = 5;
	public boolean[] presseda = new boolean[20];

	public static void disableControllers() {
		controllersInited = true;
	}

	public Input(int int1) {
		this.init(int1);
	}

	public void setDoubleClickInterval(int int1) {
		this.doubleClickDelay = int1;
	}

	public void setMouseClickTolerance(int int1) {
		this.mouseClickTolerance = int1;
	}

	public void setScale(float float1, float float2) {
		this.scaleX = float1;
		this.scaleY = float2;
	}

	public void setOffset(float float1, float float2) {
		this.xoffset = float1;
		this.yoffset = float2;
	}

	public void resetInputTransform() {
		this.setOffset(0.0F, 0.0F);
		this.setScale(1.0F, 1.0F);
	}

	public void addListener(InputListener inputListener) {
		this.addKeyListener(inputListener);
		this.addMouseListener(inputListener);
		this.addControllerListener(inputListener);
	}

	public void addKeyListener(KeyListener keyListener) {
		this.keyListenersToAdd.add(keyListener);
	}

	private void addKeyListenerImpl(KeyListener keyListener) {
		if (!this.keyListeners.contains(keyListener)) {
			this.keyListeners.add(keyListener);
			this.allListeners.add(keyListener);
		}
	}

	public void addMouseListener(MouseListener mouseListener) {
		this.mouseListenersToAdd.add(mouseListener);
	}

	private void addMouseListenerImpl(MouseListener mouseListener) {
		if (!this.mouseListeners.contains(mouseListener)) {
			this.mouseListeners.add(mouseListener);
			this.allListeners.add(mouseListener);
		}
	}

	public void addControllerListener(ControllerListener controllerListener) {
		if (!this.controllerListeners.contains(controllerListener)) {
			this.controllerListeners.add(controllerListener);
			this.allListeners.add(controllerListener);
		}
	}

	public void removeAllListeners() {
		this.removeAllKeyListeners();
		this.removeAllMouseListeners();
		this.removeAllControllerListeners();
	}

	public void removeAllKeyListeners() {
		this.allListeners.removeAll(this.keyListeners);
		this.keyListeners.clear();
	}

	public void removeAllMouseListeners() {
		this.allListeners.removeAll(this.mouseListeners);
		this.mouseListeners.clear();
	}

	public void removeAllControllerListeners() {
		this.allListeners.removeAll(this.controllerListeners);
		this.controllerListeners.clear();
	}

	public void addPrimaryListener(InputListener inputListener) {
		this.removeListener(inputListener);
		this.keyListeners.add(0, inputListener);
		this.mouseListeners.add(0, inputListener);
		this.controllerListeners.add(0, inputListener);
		this.allListeners.add(inputListener);
	}

	public void removeListener(InputListener inputListener) {
		this.removeKeyListener(inputListener);
		this.removeMouseListener(inputListener);
		this.removeControllerListener(inputListener);
	}

	public void removeKeyListener(KeyListener keyListener) {
		this.keyListeners.remove(keyListener);
		if (!this.mouseListeners.contains(keyListener) && !this.controllerListeners.contains(keyListener)) {
			this.allListeners.remove(keyListener);
		}
	}

	public void removeControllerListener(ControllerListener controllerListener) {
		this.controllerListeners.remove(controllerListener);
		if (!this.mouseListeners.contains(controllerListener) && !this.keyListeners.contains(controllerListener)) {
			this.allListeners.remove(controllerListener);
		}
	}

	public void removeMouseListener(MouseListener mouseListener) {
		this.mouseListeners.remove(mouseListener);
		if (!this.controllerListeners.contains(mouseListener) && !this.keyListeners.contains(mouseListener)) {
			this.allListeners.remove(mouseListener);
		}
	}

	void init(int int1) {
		this.height = int1;
		this.lastMouseX = this.getMouseX();
		this.lastMouseY = this.getMouseY();
	}

	public static String getKeyName(int int1) {
		return Keyboard.getKeyName(int1);
	}

	public boolean isKeyPressed(int int1) {
		if (this.pressed[int1]) {
			this.pressed[int1] = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean isMousePressed(int int1) {
		if (this.mousePressed[int1]) {
			this.mousePressed[int1] = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean isControlPressed(int int1) {
		return this.isControlPressed(int1, 0);
	}

	public boolean isControlPressed(int int1, int int2) {
		if (this.controllerPressed[int2][int1]) {
			this.controllerPressed[int2][int1] = false;
			return true;
		} else {
			return false;
		}
	}

	public void clearControlPressedRecord() {
		for (int int1 = 0; int1 < controllers.size(); ++int1) {
			Arrays.fill(this.controllerPressed[int1], false);
		}
	}

	public void clearKeyPressedRecord() {
		Arrays.fill(this.pressed, false);
	}

	public void clearMousePressedRecord() {
		Arrays.fill(this.mousePressed, false);
	}

	public boolean isKeyDown(int int1) {
		return Keyboard.isKeyDown(int1);
	}

	public int getAbsoluteMouseX() {
		return Mouse.getX();
	}

	public int getAbsoluteMouseY() {
		return this.height - Mouse.getY();
	}

	public int getMouseX() {
		return (int)((float)Mouse.getX() * this.scaleX + this.xoffset);
	}

	public int getMouseY() {
		return (int)((float)(this.height - Mouse.getY()) * this.scaleY + this.yoffset);
	}

	public boolean isMouseButtonDown(int int1) {
		return Mouse.isButtonDown(int1);
	}

	private boolean anyMouseDown() {
		for (int int1 = 0; int1 < 3; ++int1) {
			if (Mouse.isButtonDown(int1)) {
				return true;
			}
		}

		return false;
	}

	public int getControllerCount() {
		this.initControllers();
		return controllers.size();
	}

	public int getAxisCount(int int1) {
		return ((Controller)controllers.get(int1)).getAxisCount();
	}

	public float getAxisValue(int int1, int int2) {
		float float1 = ((Controller)controllers.get(int1)).getAxisValue(int2);
		return float1;
	}

	public String getAxisName(int int1, int int2) {
		return ((Controller)controllers.get(int1)).getAxisName(int2);
	}

	public boolean isControllerLeft(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerLeft(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getXAxisValue() < -0.5F || ((Controller)controllers.get(int1)).getPovX() < -0.5F;
		}
	}

	public boolean isControllerLeftD(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerLeftD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getPovX() < -0.5F;
		}
	}

	public boolean isControllerRight(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerRight(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getXAxisValue() > 0.5F || ((Controller)controllers.get(int1)).getPovX() > 0.5F;
		}
	}

	public boolean isControllerRightD(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerRightD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getPovX() > 0.5F;
		}
	}

	public boolean isControllerUp(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerUp(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getYAxisValue() < -0.5F || ((Controller)controllers.get(int1)).getPovY() < -0.5F;
		}
	}

	public boolean isControllerUpD(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerUpD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getPovY() < -0.5F;
		}
	}

	public boolean isControllerDown(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerDown(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getYAxisValue() > 0.5F || ((Controller)controllers.get(int1)).getPovY() > 0.5F;
		}
	}

	public boolean isControllerDownD(int int1) {
		if (int1 >= this.getControllerCount()) {
			return false;
		} else if (int1 == -1) {
			for (int int2 = 0; int2 < controllers.size(); ++int2) {
				if (this.isControllerDownD(int2)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int1)).getPovY() > 0.5F;
		}
	}

	public boolean isButtonPressed(int int1, int int2) {
		if (int2 >= this.getControllerCount()) {
			return false;
		} else if (int2 == -1) {
			for (int int3 = 0; int3 < controllers.size(); ++int3) {
				if (this.isButtonPressed(int1, int3)) {
					return true;
				}
			}

			return false;
		} else {
			return ((Controller)controllers.get(int2)).isButtonPressed(int1);
		}
	}

	public boolean isButtonPressedD(int int1, int int2) {
		if (int2 >= this.getControllerCount()) {
			return false;
		} else if (int2 == -1) {
			for (int int3 = 0; int3 < controllers.size(); ++int3) {
				if (this.isButtonPressed(int1, int3)) {
					return true;
				}
			}

			return false;
		} else if (int1 >= 0 && int1 < ((Controller)controllers.get(int2)).getButtonCount()) {
			boolean boolean1 = this.controllerPressed[int2][int1];
			return boolean1;
		} else {
			return false;
		}
	}

	public boolean isButton1Pressed(int int1) {
		return this.isButtonPressed(0, int1);
	}

	public boolean isButton2Pressed(int int1) {
		return this.isButtonPressed(1, int1);
	}

	public boolean isButton3Pressed(int int1) {
		return this.isButtonPressed(2, int1);
	}

	public void initControllers() {
		if (!controllersInited) {
			controllersInited = true;
			try {
				XInput.init();
				ControllerEnvironment controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();
				net.java.games.input.Controller[] controllerArray = controllerEnvironment.getControllers();
				net.java.games.input.Controller[] controllerArray2 = controllerArray;
				int int1 = controllerArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					net.java.games.input.Controller controller = controllerArray2[int2];
					if (!controller.getType().equals(Type.KEYBOARD) && !controller.getType().equals(Type.MOUSE)) {
						try {
							controller.poll();
						} catch (Throwable throwable) {
						}
					}
				}

				Controllers.create();
				int int3 = Controllers.getControllerCount();
				for (int1 = 0; int1 < int3; ++int1) {
					Controller controller2 = Controllers.getController(int1);
					for (int int4 = 0; int4 < controller2.getAxisCount(); ++int4) {
						controller2.setDeadZone(int4, 0.2F);
					}

					if (controller2.getButtonCount() >= 3 && controller2.getButtonCount() < 100) {
						controllers.add(controller2);
					}
				}
			} catch (LWJGLException lWJGLException) {
				if (lWJGLException.getCause() instanceof ClassNotFoundException) {
				}
			} catch (NoClassDefFoundError noClassDefFoundError) {
			}
		}
	}

	public void consumeEvent() {
		this.consumed = true;
	}

	private int resolveEventKey(int int1, char char1) {
		return char1 != '=' && int1 != 0 ? int1 : 13;
	}

	public void considerDoubleClick(int int1, int int2, int int3) {
		if (this.doubleClickTimeout == 0L) {
			this.clickX = int2;
			this.clickY = int3;
			this.clickButton = int1;
			this.doubleClickTimeout = System.currentTimeMillis() + (long)this.doubleClickDelay;
			this.fireMouseClicked(int1, int2, int3, 1);
		} else if (this.clickButton == int1 && System.currentTimeMillis() < this.doubleClickTimeout) {
			this.fireMouseClicked(int1, int2, int3, 2);
			this.doubleClickTimeout = 0L;
		}
	}

	public void poll(int int1, int int2) {
		if (this.paused) {
			while (Keyboard.next()) {
			}

			while (Mouse.next()) {
			}
		} else {
			int int3;
			for (int3 = 0; int3 < this.keyListenersToAdd.size(); ++int3) {
				this.addKeyListenerImpl((KeyListener)this.keyListenersToAdd.get(int3));
			}

			this.keyListenersToAdd.clear();
			for (int3 = 0; int3 < this.mouseListenersToAdd.size(); ++int3) {
				this.addMouseListenerImpl((MouseListener)this.mouseListenersToAdd.get(int3));
			}

			this.mouseListenersToAdd.clear();
			if (this.doubleClickTimeout != 0L && System.currentTimeMillis() > this.doubleClickTimeout) {
				this.doubleClickTimeout = 0L;
			}

			this.height = int2;
			ControlledInputReciever controlledInputReciever;
			Iterator iterator;
			if (!this.allListeners.isEmpty()) {
				iterator = this.allListeners.iterator();
				while (iterator.hasNext()) {
					controlledInputReciever = (ControlledInputReciever)iterator.next();
					controlledInputReciever.inputStarted();
				}
			}

			while (true) {
				KeyListener keyListener;
				int int4;
				while (Keyboard.next()) {
					if (Keyboard.getEventKeyState()) {
						int3 = this.resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
						this.keys[int3] = Keyboard.getEventCharacter();
						this.pressed[int3] = true;
						this.nextRepeat[int3] = System.currentTimeMillis() + (long)this.keyRepeatInitial;
						this.consumed = false;
						for (int4 = 0; int4 < this.keyListeners.size(); ++int4) {
							keyListener = (KeyListener)this.keyListeners.get(int4);
							if (keyListener.isAcceptingInput()) {
								keyListener.keyPressed(int3, Keyboard.getEventCharacter());
								if (this.consumed) {
									break;
								}
							}
						}
					} else {
						int3 = this.resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
						this.nextRepeat[int3] = 0L;
						this.consumed = false;
						for (int4 = 0; int4 < this.keyListeners.size(); ++int4) {
							keyListener = (KeyListener)this.keyListeners.get(int4);
							if (keyListener.isAcceptingInput()) {
								keyListener.keyReleased(int3, this.keys[int3]);
								if (this.consumed) {
									break;
								}
							}
						}
					}
				}

				while (true) {
					while (true) {
						MouseListener mouseListener;
						int int5;
						while (Mouse.next()) {
							if (Mouse.getEventButton() >= 0) {
								if (Mouse.getEventButtonState()) {
									this.consumed = false;
									this.mousePressed[Mouse.getEventButton()] = true;
									this.pressedX = (int)(this.xoffset + (float)Mouse.getEventX() * this.scaleX);
									this.pressedY = (int)(this.yoffset + (float)(int2 - Mouse.getEventY()) * this.scaleY);
									for (int3 = 0; int3 < this.mouseListeners.size(); ++int3) {
										mouseListener = (MouseListener)this.mouseListeners.get(int3);
										if (mouseListener.isAcceptingInput()) {
											mouseListener.mousePressed(Mouse.getEventButton(), this.pressedX, this.pressedY);
											if (this.consumed) {
												break;
											}
										}
									}
								} else {
									this.consumed = false;
									this.mousePressed[Mouse.getEventButton()] = false;
									int3 = (int)(this.xoffset + (float)Mouse.getEventX() * this.scaleX);
									int4 = (int)(this.yoffset + (float)(int2 - Mouse.getEventY()) * this.scaleY);
									if (this.pressedX != -1 && this.pressedY != -1 && Math.abs(this.pressedX - int3) < this.mouseClickTolerance && Math.abs(this.pressedY - int4) < this.mouseClickTolerance) {
										this.considerDoubleClick(Mouse.getEventButton(), int3, int4);
										this.pressedX = this.pressedY = -1;
									}

									for (int5 = 0; int5 < this.mouseListeners.size(); ++int5) {
										MouseListener mouseListener2 = (MouseListener)this.mouseListeners.get(int5);
										if (mouseListener2.isAcceptingInput()) {
											mouseListener2.mouseReleased(Mouse.getEventButton(), int3, int4);
											if (this.consumed) {
												break;
											}
										}
									}
								}
							} else {
								if (Mouse.isGrabbed() && (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0)) {
									this.consumed = false;
									for (int3 = 0; int3 < this.mouseListeners.size(); ++int3) {
										mouseListener = (MouseListener)this.mouseListeners.get(int3);
										if (mouseListener.isAcceptingInput()) {
											if (this.anyMouseDown()) {
												mouseListener.mouseDragged(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
											} else {
												mouseListener.mouseMoved(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
											}

											if (this.consumed) {
												break;
											}
										}
									}
								}

								int3 = Mouse.getEventDWheel();
								this.wheel += int3;
								if (int3 != 0) {
									this.consumed = false;
									for (int4 = 0; int4 < this.mouseListeners.size(); ++int4) {
										MouseListener mouseListener3 = (MouseListener)this.mouseListeners.get(int4);
										if (mouseListener3.isAcceptingInput()) {
											mouseListener3.mouseWheelMoved(int3);
											if (this.consumed) {
												break;
											}
										}
									}
								}
							}
						}

						if (!this.displayActive) {
							this.lastMouseX = this.getMouseX();
							this.lastMouseY = this.getMouseY();
						} else if (this.lastMouseX != this.getMouseX() || this.lastMouseY != this.getMouseY()) {
							this.consumed = false;
							for (int3 = 0; int3 < this.mouseListeners.size(); ++int3) {
								mouseListener = (MouseListener)this.mouseListeners.get(int3);
								if (mouseListener.isAcceptingInput()) {
									if (this.anyMouseDown()) {
										mouseListener.mouseDragged(this.lastMouseX, this.lastMouseY, this.getMouseX(), this.getMouseY());
									} else {
										mouseListener.mouseMoved(this.lastMouseX, this.lastMouseY, this.getMouseX(), this.getMouseY());
									}

									if (this.consumed) {
										break;
									}
								}
							}

							this.lastMouseX = this.getMouseX();
							this.lastMouseY = this.getMouseY();
						}

						if (controllersInited) {
							for (int3 = 0; int3 < this.getControllerCount(); ++int3) {
								int4 = ((Controller)controllers.get(int3)).getButtonCount();
								for (int5 = 0; int5 < int4; ++int5) {
									if (this.controls[int3][int5] && !this.isButtonPressed(int5, int3)) {
										this.controls[int3][int5] = false;
										this.controllerPressed[int3][int5] = false;
										this.fireControlRelease(int5, int3);
									} else if (!this.controls[int3][int5] && this.isButtonPressed(int5, int3)) {
										this.controllerPressed[int3][int5] = true;
										this.controls[int3][int5] = true;
										this.fireControlPress(int5, int3);
										JoypadManager.instance.onPressed(int3, int5);
									}
								}

								int4 = ((Controller)controllers.get(int3)).getAxisCount();
								for (int5 = 0; int5 < int4; ++int5) {
									if (((Controller)controllers.get(int3)).getAxisValue(int5) < -0.5F) {
										JoypadManager.instance.onPressedAxisNeg(int3, int5);
									}

									if (((Controller)controllers.get(int3)).getAxisValue(int5) > 0.5F) {
										JoypadManager.instance.onPressedAxis(int3, int5);
									}
								}

								float float1 = ((Controller)controllers.get(int3)).getPovX();
								float float2 = ((Controller)controllers.get(int3)).getPovY();
								if (float1 != this.controllerPov[int3][0] || float2 != this.controllerPov[int3][1]) {
									this.controllerPov[int3][0] = float1;
									this.controllerPov[int3][1] = float2;
									JoypadManager.instance.onPressedPov(int3);
								}
							}
						}

						if (this.keyRepeat) {
							for (int3 = 0; int3 < 1024; ++int3) {
								if (this.pressed[int3] && this.nextRepeat[int3] != 0L && System.currentTimeMillis() > this.nextRepeat[int3]) {
									this.nextRepeat[int3] = System.currentTimeMillis() + (long)this.keyRepeatInterval;
									this.consumed = false;
									for (int4 = 0; int4 < this.keyListeners.size(); ++int4) {
										keyListener = (KeyListener)this.keyListeners.get(int4);
										if (keyListener.isAcceptingInput()) {
											keyListener.keyPressed(int3, this.keys[int3]);
											if (this.consumed) {
												break;
											}
										}
									}
								}
							}
						}

						if (!this.allListeners.isEmpty()) {
							iterator = this.allListeners.iterator();
							while (iterator.hasNext()) {
								controlledInputReciever = (ControlledInputReciever)iterator.next();
								controlledInputReciever.inputEnded();
							}
						}

						if (Display.isCreated()) {
							this.displayActive = Display.isActive();
						}

						return;
					}
				}
			}
		}
	}

	public void enableKeyRepeat(int int1, int int2) {
		Keyboard.enableRepeatEvents(true);
	}

	public void enableKeyRepeat() {
		Keyboard.enableRepeatEvents(true);
	}

	public void disableKeyRepeat() {
		Keyboard.enableRepeatEvents(false);
	}

	public boolean isKeyRepeatEnabled() {
		return Keyboard.areRepeatEventsEnabled();
	}

	private void fireControlPress(int int1, int int2) {
		this.consumed = false;
		int int3 = int1 - 4 + 1;
		for (int int4 = 0; int4 < this.controllerListeners.size(); ++int4) {
			ControllerListener controllerListener = (ControllerListener)this.controllerListeners.get(int4);
			if (controllerListener.isAcceptingInput()) {
				switch (int1) {
				case 0: 
					controllerListener.controllerLeftPressed(int2);
					break;
				
				case 1: 
					controllerListener.controllerRightPressed(int2);
					break;
				
				case 2: 
					controllerListener.controllerUpPressed(int2);
					break;
				
				case 3: 
					controllerListener.controllerDownPressed(int2);
					break;
				
				default: 
					controllerListener.controllerButtonPressed(int2, int1 - 4 + 1);
				
				}

				if (this.consumed) {
					break;
				}
			}
		}
	}

	private void fireControlRelease(int int1, int int2) {
		this.consumed = false;
		for (int int3 = 0; int3 < this.controllerListeners.size(); ++int3) {
			ControllerListener controllerListener = (ControllerListener)this.controllerListeners.get(int3);
			if (controllerListener.isAcceptingInput()) {
				switch (int1) {
				case 0: 
					controllerListener.controllerLeftReleased(int2);
					break;
				
				case 1: 
					controllerListener.controllerRightReleased(int2);
					break;
				
				case 2: 
					controllerListener.controllerUpReleased(int2);
					break;
				
				case 3: 
					controllerListener.controllerDownReleased(int2);
					break;
				
				default: 
					controllerListener.controllerButtonReleased(int2, int1 - 4 + 1);
				
				}

				if (this.consumed) {
					break;
				}
			}
		}
	}

	private boolean isControlDwn(int int1, int int2) {
		switch (int1) {
		case 0: 
			return this.isControllerLeft(int2);
		
		case 1: 
			return this.isControllerRight(int2);
		
		case 2: 
			return this.isControllerUp(int2);
		
		case 3: 
			return this.isControllerDown(int2);
		
		default: 
			if (int1 >= 4) {
				return this.isButtonPressed(int1 - 4, int2);
			} else {
				throw new RuntimeException("Unknown control index");
			}

		
		}
	}

	public void pause() {
		this.paused = true;
		this.clearKeyPressedRecord();
		this.clearMousePressedRecord();
		this.clearControlPressedRecord();
	}

	public void resume() {
		this.paused = false;
	}

	private void fireMouseClicked(int int1, int int2, int int3, int int4) {
		this.consumed = false;
		for (int int5 = 0; int5 < this.mouseListeners.size(); ++int5) {
			MouseListener mouseListener = (MouseListener)this.mouseListeners.get(int5);
			if (mouseListener.isAcceptingInput()) {
				mouseListener.mouseClicked(int1, int2, int3, int4);
				if (this.consumed) {
					break;
				}
			}
		}
	}

	public Controller getController(int int1) {
		return (Controller)controllers.get(int1);
	}

	public int getButtonCount(int int1) {
		return ((Controller)controllers.get(int1)).getButtonCount();
	}

	public String getButtonName(int int1, int int2) {
		return ((Controller)controllers.get(int1)).getButtonName(int2);
	}

	private class NullOutputStream extends OutputStream {

		public void write(int int1) throws IOException {
		}
	}
}
