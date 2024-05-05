package zombie.vehicles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.joml.Vector2f;
import org.joml.Vector3f;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.input.GameKeyboard;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;


public final class EditVehicleState extends GameState {
	public static EditVehicleState instance;
	private EditVehicleState.LuaEnvironment m_luaEnv;
	private boolean bExit = false;
	private String m_initialScript = null;
	private final ArrayList m_gameUI = new ArrayList();
	private final ArrayList m_selfUI = new ArrayList();
	private boolean m_bSuspendUI;
	private KahluaTable m_table = null;

	public EditVehicleState() {
		instance = this;
	}

	public void enter() {
		instance = this;
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("EditVehicleState_InitUI"));
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

	public static EditVehicleState checkInstance() {
		if (instance != null) {
			if (instance.m_table != null && instance.m_table.getMetatable() != null) {
				if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
					instance = null;
				}
			} else {
				instance = null;
			}
		}

		return instance == null ? new EditVehicleState() : instance;
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

	public void setScript(String string) {
		if (this.m_table == null) {
			this.m_initialScript = string;
		} else {
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("setScript"), this.m_table, string);
		}
	}

	public Object fromLua0(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1286189703: 
			if (string.equals("getInitialScript")) {
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
			return this.m_initialScript;
		
		default: 
			throw new IllegalArgumentException("unhandled \"" + string + "\"");
		
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
				VehicleScript vehicleScript = ScriptManager.instance.getVehicle((String)object);
				if (vehicleScript == null) {
					throw new NullPointerException("vehicle script \"" + object + "\" not found");
				}

				ArrayList arrayList = this.readScript(vehicleScript.getFileName());
				if (arrayList != null) {
					this.updateScript(vehicleScript.getFileName(), arrayList, vehicleScript);
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

	private void updateScript(String string, ArrayList arrayList, VehicleScript vehicleScript) {
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
				if (string4.equals(vehicleScript.getModule().getName())) {
					String string5 = string2.substring(int2 + 1, int3).trim();
					ArrayList arrayList2 = ScriptParser.parseTokens(string5);
					for (int int4 = arrayList2.size() - 1; int4 >= 0; --int4) {
						String string6 = ((String)arrayList2.get(int4)).trim();
						if (string6.startsWith("vehicle")) {
							int2 = string6.indexOf("{");
							string3 = string6.substring(0, int2).trim();
							stringArray = string3.split("\\s+");
							String string7 = stringArray.length > 1 ? stringArray[1].trim() : "";
							if (string7.equals(vehicleScript.getName())) {
								string6 = this.vehicleScriptToText(vehicleScript, string6).trim();
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

	private String vehicleScriptToText(VehicleScript vehicleScript, String string) {
		float float1 = vehicleScript.getModelScale();
		ScriptParser.Block block = ScriptParser.parse(string);
		block = (ScriptParser.Block)block.children.get(0);
		VehicleScript.Model model = vehicleScript.getModel();
		ScriptParser.Block block2 = block.getBlock("model", (String)null);
		if (model != null && block2 != null) {
			float float2 = vehicleScript.getModelScale();
			block2.setValue("scale", String.format(Locale.US, "%.4f", float2));
			Vector3f vector3f = vehicleScript.getModel().getOffset();
			block2.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", vector3f.x / float1, vector3f.y / float1, vector3f.z / float1));
		}

		ArrayList arrayList = new ArrayList();
		int int1;
		ScriptParser.Block block3;
		for (int1 = 0; int1 < block.children.size(); ++int1) {
			block3 = (ScriptParser.Block)block.children.get(int1);
			if ("physics".equals(block3.type)) {
				if (arrayList.size() == vehicleScript.getPhysicsShapeCount()) {
					block.elements.remove(block3);
					block.children.remove(int1);
					--int1;
				} else {
					arrayList.add(block3);
				}
			}
		}

		for (int1 = 0; int1 < vehicleScript.getPhysicsShapeCount(); ++int1) {
			VehicleScript.PhysicsShape physicsShape = vehicleScript.getPhysicsShape(int1);
			boolean boolean1 = int1 < arrayList.size();
			ScriptParser.Block block4 = boolean1 ? (ScriptParser.Block)arrayList.get(int1) : new ScriptParser.Block();
			block4.type = "physics";
			block4.id = physicsShape.getTypeString();
			if (boolean1) {
				block4.elements.clear();
				block4.children.clear();
				block4.values.clear();
			}

			block4.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getOffset().x() / float1, physicsShape.getOffset().y() / float1, physicsShape.getOffset().z() / float1));
			if (physicsShape.type == 1) {
				block4.setValue("extents", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getExtents().x() / float1, physicsShape.getExtents().y() / float1, physicsShape.getExtents().z() / float1));
				block4.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getRotate().x(), physicsShape.getRotate().y(), physicsShape.getRotate().z()));
			}

			if (physicsShape.type == 2) {
				block4.setValue("radius", String.format(Locale.US, "%.4f", physicsShape.getRadius() / float1));
			}

			if (!boolean1) {
				block.elements.add(block4);
				block.children.add(block4);
			}
		}

		for (int1 = block.children.size() - 1; int1 >= 0; --int1) {
			block3 = (ScriptParser.Block)block.children.get(int1);
			if ("attachment".equals(block3.type)) {
				block.elements.remove(block3);
				block.children.remove(int1);
			}
		}

		ScriptParser.Block block5;
		for (int1 = 0; int1 < vehicleScript.getAttachmentCount(); ++int1) {
			ModelAttachment modelAttachment = vehicleScript.getAttachment(int1);
			block5 = block.getBlock("attachment", modelAttachment.getId());
			if (block5 == null) {
				block5 = new ScriptParser.Block();
				block5.type = "attachment";
				block5.id = modelAttachment.getId();
				block.elements.add(block5);
				block.children.add(block5);
			}

			block5.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getOffset().x() / float1, modelAttachment.getOffset().y() / float1, modelAttachment.getOffset().z() / float1));
			block5.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z()));
			if (modelAttachment.getBone() != null) {
				block5.setValue("bone", modelAttachment.getBone());
			}

			if (modelAttachment.getCanAttach() != null) {
				block5.setValue("canAttach", PZArrayUtil.arrayToString((Iterable)modelAttachment.getCanAttach(), "", "", ","));
			}

			if (modelAttachment.getZOffset() != 0.0F) {
				block5.setValue("zoffset", String.format(Locale.US, "%.4f", modelAttachment.getZOffset()));
			}

			if (!modelAttachment.isUpdateConstraint()) {
				block5.setValue("updateconstraint", "false");
			}
		}

		Vector3f vector3f2 = vehicleScript.getExtents();
		block.setValue("extents", String.format(Locale.US, "%.4f %.4f %.4f", vector3f2.x / float1, vector3f2.y / float1, vector3f2.z / float1));
		vector3f2 = vehicleScript.getPhysicsChassisShape();
		block.setValue("physicsChassisShape", String.format(Locale.US, "%.4f %.4f %.4f", vector3f2.x / float1, vector3f2.y / float1, vector3f2.z / float1));
		vector3f2 = vehicleScript.getCenterOfMassOffset();
		block.setValue("centerOfMassOffset", String.format(Locale.US, "%.4f %.4f %.4f", vector3f2.x / float1, vector3f2.y / float1, vector3f2.z / float1));
		Vector2f vector2f = vehicleScript.getShadowExtents();
		boolean boolean2 = block.getValue("shadowExtents") != null;
		block.setValue("shadowExtents", String.format(Locale.US, "%.4f %.4f", vector2f.x / float1, vector2f.y / float1));
		if (!boolean2) {
			block.moveValueAfter("shadowExtents", "centerOfMassOffset");
		}

		vector2f = vehicleScript.getShadowOffset();
		boolean2 = block.getValue("shadowOffset") != null;
		block.setValue("shadowOffset", String.format(Locale.US, "%.4f %.4f", vector2f.x / float1, vector2f.y / float1));
		if (!boolean2) {
			block.moveValueAfter("shadowOffset", "shadowExtents");
		}

		for (int1 = 0; int1 < vehicleScript.getAreaCount(); ++int1) {
			VehicleScript.Area area = vehicleScript.getArea(int1);
			block5 = block.getBlock("area", area.getId());
			if (block5 != null) {
				block5.setValue("xywh", String.format(Locale.US, "%.4f %.4f %.4f %.4f", area.getX() / (double)float1, area.getY() / (double)float1, area.getW() / (double)float1, area.getH() / (double)float1));
			}
		}

		for (int1 = 0; int1 < vehicleScript.getPassengerCount(); ++int1) {
			VehicleScript.Passenger passenger = vehicleScript.getPassenger(int1);
			block5 = block.getBlock("passenger", passenger.getId());
			if (block5 != null) {
				Iterator iterator = passenger.positions.iterator();
				while (iterator.hasNext()) {
					VehicleScript.Position position = (VehicleScript.Position)iterator.next();
					ScriptParser.Block block6 = block5.getBlock("position", position.id);
					if (block6 != null) {
						block6.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", position.offset.x / float1, position.offset.y / float1, position.offset.z / float1));
						block6.setValue("rotate", String.format(Locale.US, "%.4f %.4f %.4f", position.rotate.x / float1, position.rotate.y / float1, position.rotate.z / float1));
					}
				}
			}
		}

		for (int1 = 0; int1 < vehicleScript.getWheelCount(); ++int1) {
			VehicleScript.Wheel wheel = vehicleScript.getWheel(int1);
			block5 = block.getBlock("wheel", wheel.getId());
			if (block5 != null) {
				block5.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", wheel.offset.x / float1, wheel.offset.y / float1, wheel.offset.z / float1));
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

	public static final class LuaEnvironment {
		public J2SEPlatform platform;
		public KahluaTable env;
		public KahluaThread thread;
		public LuaCaller caller;

		public LuaEnvironment(J2SEPlatform j2SEPlatform, KahluaConverterManager kahluaConverterManager, KahluaTable kahluaTable) {
			this.platform = j2SEPlatform;
			this.env = kahluaTable;
			this.thread = LuaManager.thread;
			this.caller = LuaManager.caller;
		}
	}
}
