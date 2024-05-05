package org.lwjglx.input;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import org.lwjglx.opengl.Display;
import zombie.core.opengl.RenderThread;
import zombie.input.GameKeyboard;


public class Keyboard {
	public static final int CHAR_NONE = 0;
	public static final int KEY_NONE = 0;
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
	public static final int KEY_F16 = 103;
	public static final int KEY_F17 = 104;
	public static final int KEY_F18 = 105;
	public static final int KEY_KANA = 112;
	public static final int KEY_F19 = 113;
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
	public static final int KEY_SECTION = 167;
	public static final int KEY_NUMPADCOMMA = 179;
	public static final int KEY_DIVIDE = 181;
	public static final int KEY_SYSRQ = 183;
	public static final int KEY_RMENU = 184;
	public static final int KEY_FUNCTION = 196;
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
	public static final int KEY_CLEAR = 218;
	public static final int KEY_LMETA = 219;
	public static final int KEY_LWIN = 219;
	public static final int KEY_RMETA = 220;
	public static final int KEY_RWIN = 220;
	public static final int KEY_APPS = 221;
	public static final int KEY_POWER = 222;
	public static final int KEY_SLEEP = 223;
	private static boolean repeatEvents = false;
	public static final int KEYBOARD_SIZE = 256;
	private static final String[] keyName = new String[256];
	private static final Map keyMap = new HashMap(253);

	public static void addKeyEvent(int int1, int int2) {
		GameKeyboard.getEventQueuePolling().addKeyEvent(int1, int2);
	}

	public static void addCharEvent(char char1) {
		GameKeyboard.getEventQueuePolling().addCharEvent(char1);
	}

	public static void create() {
		initKeyNames();
	}

	public static void initKeyNames() {
		if (RenderThread.RenderThread != null && Thread.currentThread() != RenderThread.RenderThread) {
			RenderThread.invokeOnRenderContext(Keyboard::initKeyNames);
		} else {
			Arrays.fill(keyName, (Object)null);
			keyMap.clear();
			Field[] fieldArray = Keyboard.class.getFields();
			try {
				Field[] fieldArray2 = fieldArray;
				int int1 = fieldArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					Field field = fieldArray2[int2];
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(Integer.TYPE) && field.getName().startsWith("KEY_") && !field.getName().endsWith("WIN")) {
						int int3 = field.getInt((Object)null);
						String string = field.getName().substring(4);
						int int4 = KeyCodes.toGlfwKey(int3);
						switch (int4) {
						case 320: 
						
						case 321: 
						
						case 322: 
						
						case 323: 
						
						case 324: 
						
						case 325: 
						
						case 326: 
						
						case 327: 
						
						case 328: 
						
						case 329: 
							string = string.replace("NUMPAD", "KP_");
							int4 = -1;
							break;
						
						case 330: 
						
						case 331: 
						
						case 332: 
						
						case 333: 
						
						case 334: 
						
						case 335: 
						
						case 336: 
							string = "KP_" + string.replace("NUMPAD", "");
							int4 = -1;
						
						}

						if (int4 != -1) {
							int int5 = GLFW.glfwGetKeyScancode(int4);
							if (int5 > 0) {
								boolean boolean1 = false;
								String string2 = GLFW.glfwGetKeyName(int4, 0);
								if (string2 != null) {
									string = string2.toUpperCase();
								}
							}
						}

						keyName[int3] = string;
						keyMap.put(string, int3);
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static boolean isKeyDown(int int1) {
		int int2 = KeyCodes.toGlfwKey(int1);
		if (int2 == -1) {
			return false;
		} else {
			int int3 = GLFW.glfwGetKey(Display.getWindow(), int2);
			return int3 == 1;
		}
	}

	public static void poll() {
	}

	public static void enableRepeatEvents(boolean boolean1) {
		repeatEvents = boolean1;
	}

	public static boolean areRepeatEventsEnabled() {
		return repeatEvents;
	}

	public static boolean isRepeatEvent() {
		return repeatEvents;
	}

	public static boolean next() {
		return GameKeyboard.getEventQueue().next();
	}

	public static int getEventKey() {
		return GameKeyboard.getEventQueue().getEventKey();
	}

	public static char getEventCharacter() {
		return GameKeyboard.getEventQueue().getEventCharacter();
	}

	public static boolean getEventKeyState() {
		return GameKeyboard.getEventQueue().getEventKeyState();
	}

	public static long getEventNanoseconds() {
		return GameKeyboard.getEventQueue().getEventNanoseconds();
	}

	public static String getKeyName(int int1) {
		return keyName[int1];
	}

	public static int getKeyIndex(String string) {
		Integer integer = (Integer)keyMap.get(string);
		return integer == null ? 0 : integer;
	}

	public static boolean isCreated() {
		return Display.isCreated();
	}

	public static void destroy() {
	}
}
