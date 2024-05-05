package zombie.input;

import org.lwjglx.input.KeyEventQueue;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.opengl.RenderThread;
import zombie.ui.UIManager;


public final class GameKeyboard {
	private static boolean[] bDown;
	private static boolean[] bLastDown;
	private static boolean[] bEatKey;
	public static boolean bNoEventsWhileLoading = false;
	public static boolean doLuaKeyPressed = true;
	private static final KeyboardStateCache s_keyboardStateCache = new KeyboardStateCache();

	public static void update() {
		if (!s_keyboardStateCache.getState().isCreated()) {
			s_keyboardStateCache.swap();
		} else {
			int int1 = s_keyboardStateCache.getState().getKeyCount();
			if (bDown == null) {
				bDown = new boolean[int1];
				bLastDown = new boolean[int1];
				bEatKey = new boolean[int1];
			}

			boolean boolean1 = Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry;
			for (int int2 = 1; int2 < int1; ++int2) {
				bLastDown[int2] = bDown[int2];
				bDown[int2] = s_keyboardStateCache.getState().isKeyDown(int2);
				if (!bDown[int2] && bLastDown[int2]) {
					if (bEatKey[int2]) {
						bEatKey[int2] = false;
						continue;
					}

					if (bNoEventsWhileLoading || boolean1 || LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRelease(int2)) {
						continue;
					}

					if (Core.bDebug && !doLuaKeyPressed) {
						System.out.println("KEY RELEASED " + int2 + " doLuaKeyPressed=false");
					}

					if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
						LuaEventManager.triggerEvent("OnKeyPressed", int2);
					}

					if (LuaManager.thread == UIManager.defaultthread) {
						LuaEventManager.triggerEvent("OnCustomUIKey", int2);
						LuaEventManager.triggerEvent("OnCustomUIKeyReleased", int2);
					}
				}

				if (bDown[int2] && bLastDown[int2]) {
					if (bNoEventsWhileLoading || boolean1 || LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRepeat(int2)) {
						continue;
					}

					if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
						LuaEventManager.triggerEvent("OnKeyKeepPressed", int2);
					}
				}

				if (bDown[int2] && !bLastDown[int2] && !bNoEventsWhileLoading && !boolean1 && !bEatKey[int2] && (LuaManager.thread != UIManager.defaultthread || !UIManager.onKeyPress(int2)) && !bEatKey[int2]) {
					if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
						LuaEventManager.triggerEvent("OnKeyStartPressed", int2);
					}

					if (LuaManager.thread == UIManager.defaultthread) {
						LuaEventManager.triggerEvent("OnCustomUIKeyPressed", int2);
					}
				}
			}

			s_keyboardStateCache.swap();
		}
	}

	public static void poll() {
		s_keyboardStateCache.poll();
	}

	public static boolean isKeyPressed(int int1) {
		return isKeyDown(int1) && !wasKeyDown(int1);
	}

	public static boolean isKeyDown(int int1) {
		if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
			return false;
		} else {
			return bDown == null ? false : bDown[int1];
		}
	}

	public static boolean wasKeyDown(int int1) {
		if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
			return false;
		} else {
			return bLastDown == null ? false : bLastDown[int1];
		}
	}

	public static void eatKeyPress(int int1) {
		if (int1 >= 0 && int1 < bEatKey.length) {
			bEatKey[int1] = true;
		}
	}

	public static void setDoLuaKeyPressed(boolean boolean1) {
		doLuaKeyPressed = boolean1;
	}

	public static KeyEventQueue getEventQueue() {
		assert Thread.currentThread() == GameWindow.GameThread;
		return s_keyboardStateCache.getState().getEventQueue();
	}

	public static KeyEventQueue getEventQueuePolling() {
		assert Thread.currentThread() == RenderThread.RenderThread;
		return s_keyboardStateCache.getStatePolling().getEventQueue();
	}
}
