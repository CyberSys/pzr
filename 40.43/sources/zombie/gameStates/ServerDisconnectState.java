package zombie.gameStates;

import zombie.GameWindow;
import zombie.IndieGL;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.input.GameKeyboard;
import zombie.iso.IsoCamera;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;


public class ServerDisconnectState extends GameState {
	private boolean keyDown = false;
	private int gridX = -1;
	private int gridY = -1;

	public void enter() {
		TutorialManager.instance.StealControl = false;
		UIManager.UI.clear();
		LuaEventManager.ResetCallbacks();
		LuaManager.call("ISServerDisconnectUI_OnServerDisconnectUI", GameWindow.kickReason);
	}

	public void exit() {
		GameWindow.kickReason = null;
	}

	public void render() {
		boolean boolean1 = true;
		int int1;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (IsoPlayer.players[int1] == null) {
				if (int1 == 0) {
					SpriteRenderer.instance.preRender();
				}
			} else {
				IsoPlayer.instance = IsoPlayer.players[int1];
				IsoCamera.CamCharacter = IsoPlayer.players[int1];
				Core.getInstance().StartFrame(int1, boolean1);
				IsoCamera.frameState.set(int1);
				boolean1 = false;
				IsoSprite.globalOffsetX = -1;
				IsoWorld.instance.render();
				Core.getInstance().EndFrame(int1);
			}
		}

		Core.getInstance().RenderOffScreenBuffer();
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (IsoPlayer.players[int1] != null) {
				Core.getInstance().StartFrameText(int1);
				IndieGL.disableAlphaTest();
				IndieGL.glDisable(2929);
				TextDrawObject.RenderBatch(int1);
				ChatElement.RenderBatch(int1);
				try {
					Core.getInstance().EndFrameText(int1);
				} catch (Exception exception) {
				}
			}
		}

		if (Core.getInstance().StartFrameUI()) {
			UIManager.render();
			String string = GameWindow.kickReason;
			if (string == null || string.isEmpty()) {
				string = "Connection to server lost";
			}

			TextManager.instance.DrawStringCentre(UIFont.Medium, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), string, 1.0, 1.0, 1.0, 1.0);
		}

		Core.getInstance().EndFrameUI();
	}

	public GameStateMachine.StateAction update() {
		if (!Core.bExiting && !GameKeyboard.isKeyDown(1)) {
			UIManager.update();
			return GameStateMachine.StateAction.Remain;
		} else {
			return GameStateMachine.StateAction.Continue;
		}
	}
}
