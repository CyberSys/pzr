package zombie.gameStates;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;


public final class AnimationViewerState extends GameState {
	public static AnimationViewerState instance;
	private EditVehicleState.LuaEnvironment m_luaEnv;
	private boolean bExit = false;
	private final ArrayList m_gameUI = new ArrayList();
	private final ArrayList m_selfUI = new ArrayList();
	private boolean m_bSuspendUI;
	private KahluaTable m_table = null;
	private final ArrayList m_clipNames = new ArrayList();
	private static final int VERSION = 1;
	private final ArrayList options = new ArrayList();
	private AnimationViewerState.BooleanDebugOption DrawGrid = new AnimationViewerState.BooleanDebugOption("DrawGrid", false);
	private AnimationViewerState.BooleanDebugOption Isometric = new AnimationViewerState.BooleanDebugOption("Isometric", false);
	private AnimationViewerState.BooleanDebugOption UseDeferredMovement = new AnimationViewerState.BooleanDebugOption("UseDeferredMovement", false);

	public void enter() {
		instance = this;
		this.load();
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("AnimationViewerState_InitUI"));
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

	public static AnimationViewerState checkInstance() {
		if (instance != null) {
			if (instance.m_table != null && instance.m_table.getMetatable() != null) {
				if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
					instance = null;
				}
			} else {
				instance = null;
			}
		}

		return instance == null ? new AnimationViewerState() : instance;
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
		ModelManager.instance.update();
		if (GameKeyboard.isKeyPressed(17)) {
			DebugOptions.instance.ModelRenderWireframe.setValue(!DebugOptions.instance.ModelRenderWireframe.getValue());
		}
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
		case -1628879070: 
			if (string.equals("getClipNames")) {
				byte1 = 1;
			}

			break;
		
		case 3127582: 
			if (string.equals("exit")) {
				byte1 = 0;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.bExit = true;
			return null;
		
		case 1: 
			if (this.m_clipNames.isEmpty()) {
				Collection collection = ModelManager.instance.getAllAnimationClips();
				Iterator iterator = collection.iterator();
				while (iterator.hasNext()) {
					AnimationClip animationClip = (AnimationClip)iterator.next();
					this.m_clipNames.add(animationClip.Name);
				}

				this.m_clipNames.sort(Comparator.naturalOrder());
			}

			return this.m_clipNames;
		
		default: 
			throw new IllegalArgumentException("unhandled \"" + string + "\"");
		
		}
	}

	public Object fromLua1(String string, Object object) {
		byte byte1 = -1;
		string.hashCode();
		switch (byte1) {
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", string, object));
		
		}
	}

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.options.get(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof BooleanConfigOption) {
			((BooleanConfigOption)configOption).setValue(boolean1);
		}
	}

	public boolean getBoolean(String string) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
	}

	public void save() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "animationViewerState-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string2, 1, this.options);
	}

	public void load() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "animationViewerState-options.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string2)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				ConfigOption configOption2 = this.getOptionByName(configOption.getName());
				if (configOption2 != null) {
					configOption2.parse(configOption.getValueAsString());
				}
			}
		}
	}

	public class BooleanDebugOption extends BooleanConfigOption {

		public BooleanDebugOption(String string, boolean boolean1) {
			super(string, boolean1);
			AnimationViewerState.this.options.add(this);
		}
	}
}
