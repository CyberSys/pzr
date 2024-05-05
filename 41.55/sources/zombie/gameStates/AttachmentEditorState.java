package zombie.gameStates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;


public final class AttachmentEditorState extends GameState {
	public static AttachmentEditorState instance;
	private EditVehicleState.LuaEnvironment m_luaEnv;
	private boolean bExit = false;
	private final ArrayList m_gameUI = new ArrayList();
	private final ArrayList m_selfUI = new ArrayList();
	private boolean m_bSuspendUI;
	private KahluaTable m_table = null;

	public void enter() {
		instance = this;
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("AttachmentEditorState_InitUI"));
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
		this.restoreGameUI();
	}

	public void render() {
		byte byte1 = 0;
		Core.getInstance().StartFrame(byte1, true);
		this.renderScene();
		Core.getInstance().EndFrame(byte1);
		Core.getInstance().RenderOffScreenBuffer();
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

	public static AttachmentEditorState checkInstance() {
		if (instance != null) {
			if (instance.m_table != null && instance.m_table.getMetatable() != null) {
				if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
					instance = null;
				}
			} else {
				instance = null;
			}
		}

		return instance == null ? new AttachmentEditorState() : instance;
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

	public Object fromLua1(String string, Object object) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case 1396535690: 
			if (string.equals("writeScript")) {
				byte1 = 0;
			}

		
		default: 
			switch (byte1) {
			case 0: 
				ModelScript modelScript = ScriptManager.instance.getModelScript((String)object);
				if (modelScript == null) {
					throw new NullPointerException("model script \"" + object + "\" not found");
				}

				ArrayList arrayList = this.readScript(modelScript.getFileName());
				if (arrayList != null) {
					this.updateScript(modelScript.getFileName(), arrayList, modelScript);
				}

				return null;
			
			default: 
				throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", string, object));
			
			}

		
		}
	}

	private ArrayList readScript(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		string = ZomboidFileSystem.instance.getString(string);
		File file = new File(string);
		try {
			FileReader fileReader = new FileReader(file);
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					String string2 = System.lineSeparator();
					String string3;
					while ((string3 = bufferedReader.readLine()) != null) {
						stringBuilder.append(string3);
						stringBuilder.append(string2);
					}
				} catch (Throwable throwable) {
					try {
						bufferedReader.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedReader.close();
			} catch (Throwable throwable3) {
				try {
					fileReader.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileReader.close();
		} catch (Throwable throwable5) {
			ExceptionLogger.logException(throwable5);
			return null;
		}

		String string4 = ScriptParser.stripComments(stringBuilder.toString());
		return ScriptParser.parseTokens(string4);
	}

	private void updateScript(String string, ArrayList arrayList, ModelScript modelScript) {
		string = ZomboidFileSystem.instance.getString(string);
		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			String string2 = ((String)arrayList.get(int1)).trim();
			int int2 = string2.indexOf("{");
			int int3 = string2.lastIndexOf("}");
			String string3 = string2.substring(0, int2);
			if (string3.startsWith("module")) {
				string3 = string2.substring(0, int2).trim();
				String[] stringArray = string3.split("\\s+");
				String string4 = stringArray.length > 1 ? stringArray[1].trim() : "";
				if (string4.equals(modelScript.getModule().getName())) {
					String string5 = string2.substring(int2 + 1, int3).trim();
					ArrayList arrayList2 = ScriptParser.parseTokens(string5);
					for (int int4 = arrayList2.size() - 1; int4 >= 0; --int4) {
						String string6 = ((String)arrayList2.get(int4)).trim();
						if (string6.startsWith("model")) {
							int2 = string6.indexOf("{");
							string3 = string6.substring(0, int2).trim();
							stringArray = string3.split("\\s+");
							String string7 = stringArray.length > 1 ? stringArray[1].trim() : "";
							if (string7.equals(modelScript.getName())) {
								string6 = this.modelScriptToText(modelScript, string6).trim();
								arrayList2.set(int4, string6);
								String string8 = System.lineSeparator();
								String string9 = String.join(string8 + "\t", arrayList2);
								string9 = "module " + string4 + string8 + "{" + string8 + "\t" + string9 + string8 + "}" + string8;
								arrayList.set(int1, string9);
								this.writeScript(string, arrayList);
								return;
							}
						}
					}
				}
			}
		}
	}

	private String modelScriptToText(ModelScript modelScript, String string) {
		ScriptParser.Block block = ScriptParser.parse(string);
		block = (ScriptParser.Block)block.children.get(0);
		int int1;
		for (int1 = block.children.size() - 1; int1 >= 0; --int1) {
			ScriptParser.Block block2 = (ScriptParser.Block)block.children.get(int1);
			if ("attachment".equals(block2.type)) {
				block.elements.remove(block2);
				block.children.remove(int1);
			}
		}

		for (int1 = 0; int1 < modelScript.getAttachmentCount(); ++int1) {
			ModelAttachment modelAttachment = modelScript.getAttachment(int1);
			ScriptParser.Block block3 = block.getBlock("attachment", modelAttachment.getId());
			if (block3 == null) {
				block3 = new ScriptParser.Block();
				block3.type = "attachment";
				block3.id = modelAttachment.getId();
				block3.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getOffset().x(), modelAttachment.getOffset().y(), modelAttachment.getOffset().z()));
				block3.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z()));
				if (modelAttachment.getBone() != null) {
					block3.setValue("bone", modelAttachment.getBone());
				}

				block.elements.add(block3);
				block.children.add(block3);
			} else {
				block3.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getOffset().x(), modelAttachment.getOffset().y(), modelAttachment.getOffset().z()));
				block3.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z()));
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		String string2 = System.lineSeparator();
		block.prettyPrint(1, stringBuilder, string2);
		return stringBuilder.toString();
	}

	private void writeScript(String string, ArrayList arrayList) {
		String string2 = ZomboidFileSystem.instance.getString(string);
		File file = new File(string2);
		try {
			FileWriter fileWriter = new FileWriter(file);
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				try {
					DebugLog.General.printf("writing %s\n", string);
					Iterator iterator = arrayList.iterator();
					while (true) {
						if (!iterator.hasNext()) {
							this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("wroteScript"), this.m_table, string2);
							break;
						}

						String string3 = (String)iterator.next();
						bufferedWriter.write(string3);
					}
				} catch (Throwable throwable) {
					try {
						bufferedWriter.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedWriter.close();
			} catch (Throwable throwable3) {
				try {
					fileWriter.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileWriter.close();
		} catch (Throwable throwable5) {
			ExceptionLogger.logException(throwable5);
		}
	}
}
