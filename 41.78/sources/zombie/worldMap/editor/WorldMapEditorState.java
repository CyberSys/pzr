package zombie.worldMap.editor;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.input.GameKeyboard;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;


public final class WorldMapEditorState extends GameState {
	public static WorldMapEditorState instance;
	private EditVehicleState.LuaEnvironment m_luaEnv;
	private boolean bExit = false;
	private final ArrayList m_gameUI = new ArrayList();
	private final ArrayList m_selfUI = new ArrayList();
	private boolean m_bSuspendUI;
	private KahluaTable m_table = null;

	public void enter() {
		instance = this;
		this.load();
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("WorldMapEditor_InitUI"), (Object)this);
			if (this.m_table != null && this.m_table.getMetatable() != null) {
				this.m_table.getMetatable().rawset("_LUA_RELOADED_CHECK", Boolean.FALSE);
			}
		} else {
			UIManager.UI.addAll(this.m_selfUI);
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("showUI"), (Object)this.m_table);
		}

		this.bExit = false;
	}

	public void yield() {
		this.restoreGameUI();
	}

	public void reenter() {
		this.saveGameUI();
	}

	public void exit() {
		this.save();
		this.restoreGameUI();
	}

	public void render() {
		byte byte1 = 0;
		Core.getInstance().StartFrame(byte1, true);
		this.renderScene();
		Core.getInstance().EndFrame(byte1);
		Core.getInstance().RenderOffScreenBuffer();
		UIManager.useUIFBO = Core.getInstance().supportsFBO() && Core.OptionUIFBO;
		if (Core.getInstance().StartFrameUI()) {
			this.renderUI();
		}

		Core.getInstance().EndFrameUI();
	}

	public GameStateMachine.StateAction update() {
		if (!this.bExit && !GameKeyboard.isKeyPressed(65)) {
			this.updateScene();
			return GameStateMachine.StateAction.Remain;
		} else {
			return GameStateMachine.StateAction.Continue;
		}
	}

	public static WorldMapEditorState checkInstance() {
		if (instance != null) {
			if (instance.m_table != null && instance.m_table.getMetatable() != null) {
				if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
					instance = null;
				}
			} else {
				instance = null;
			}
		}

		return instance == null ? new WorldMapEditorState() : instance;
	}

	private void saveGameUI() {
		this.m_gameUI.clear();
		this.m_gameUI.addAll(UIManager.UI);
		UIManager.UI.clear();
		this.m_bSuspendUI = UIManager.bSuspend;
		UIManager.bSuspend = false;
		UIManager.setShowPausedMessage(false);
		UIManager.defaultthread = this.m_luaEnv.thread;
	}

	private void restoreGameUI() {
		this.m_selfUI.clear();
		this.m_selfUI.addAll(UIManager.UI);
		UIManager.UI.clear();
		UIManager.UI.addAll(this.m_gameUI);
		UIManager.bSuspend = this.m_bSuspendUI;
		UIManager.setShowPausedMessage(true);
		UIManager.defaultthread = LuaManager.thread;
	}

	private void updateScene() {
	}

	private void renderScene() {
	}

	private void renderUI() {
		UIManager.render();
	}

	public void setTable(KahluaTable kahluaTable) {
		this.m_table = kahluaTable;
	}

	public Object fromLua0(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case 3127582: 
			if (string.equals("exit")) {
				byte1 = 0;
			}

		
		default: 
			switch (byte1) {
			case 0: 
				this.bExit = true;
				return null;
			
			default: 
				throw new IllegalArgumentException("unhandled \"" + string + "\"");
			
			}

		
		}
	}

	public void save() {
	}

	public void load() {
	}
}
