package zombie.input;

import org.lwjgl.input.Keyboard;
import zombie.FrameLoader;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.ui.UIManager;


public class GameKeyboard {
	static boolean[] bDown;
	static boolean[] bLastDown;
	static boolean[] bEatKey;
	static int isDownFor = 0;
	public static boolean doLuaKeyPressed = true;

	public static void update() {
		if (!FrameLoader.bDedicated) {
			short short1 = 256;
			if (bDown == null) {
				bDown = new boolean[short1];
				bLastDown = new boolean[short1];
				bEatKey = new boolean[short1];
			}

			for (int int1 = 1; int1 < short1; ++int1) {
				bLastDown[int1] = bDown[int1];
				bDown[int1] = Keyboard.isKeyDown(int1);
				if (bDown[Core.getInstance().getKey("Run")]) {
					++isDownFor;
				}

				if (!bDown[int1] && bLastDown[int1]) {
					if (bEatKey[int1]) {
						bEatKey[int1] = false;
						continue;
					}

					if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
						continue;
					}

					if (Core.bDebug && !doLuaKeyPressed) {
						System.out.println("KEY RELEASED " + int1 + " doLuaKeyPressed=false");
					}

					if (int1 == Core.getInstance().getKey("Run") && Core.getInstance().isToggleToRun() && isDownFor < 5000) {
						IsoPlayer.instance.setForceRun(!IsoPlayer.instance.isForceRun());
					}

					if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
						LuaEventManager.triggerEvent("OnKeyPressed", int1);
					}

					if (LuaManager.thread == UIManager.defaultthread) {
						LuaEventManager.triggerEvent("OnCustomUIKey", int1);
						LuaEventManager.triggerEvent("OnCustomUIKeyReleased", int1);
					}

					isDownFor = 0;
				}

				if (bDown[int1] && LuaManager.thread == UIManager.defaultthread) {
					LuaEventManager.triggerEvent("OnKeyKeepPressed", int1);
				}

				if (bDown[int1] && !bLastDown[int1] && (Core.CurrentTextEntryBox == null || !Core.CurrentTextEntryBox.DoingTextEntry) && !bEatKey[int1]) {
					if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
						LuaEventManager.triggerEvent("OnKeyStartPressed", int1);
					}

					if (LuaManager.thread == UIManager.defaultthread) {
						LuaEventManager.triggerEvent("OnCustomUIKeyPressed", int1);
					}
				}
			}
		}
	}

	public static boolean isKeyDown(int int1) {
		if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
			return false;
		} else {
			return bDown == null ? false : bDown[int1];
		}
	}

	public static boolean wasKeyDown(int int1) {
		return Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry ? false : bLastDown[int1];
	}

	public static void eatKeyPress(int int1) {
		if (int1 >= 0 && int1 < bEatKey.length) {
			bEatKey[int1] = true;
		}
	}

	public static void setDoLuaKeyPressed(boolean boolean1) {
		doLuaKeyPressed = boolean1;
	}
}
