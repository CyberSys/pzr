package zombie.scripting.commands.Lua;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;
import zombie.scripting.commands.BaseCommand;


public class LuaCall extends BaseCommand {
	boolean invert = false;
	String position;
	String val;
	Object[] paramProper;
	public String func = "";
	String[] params;

	public boolean getValue() {
		Object[] objectArray;
		LuaClosure luaClosure;
		String[] stringArray;
		KahluaTable kahluaTable;
		int int1;
		if (this.params.length == 1) {
			this.func = this.params[0].replace("\"", "");
			luaClosure = null;
			stringArray = this.func.split("\\.");
			kahluaTable = LuaManager.env;
			if (stringArray.length <= 1) {
				luaClosure = (LuaClosure)kahluaTable.rawget(this.func);
			} else {
				for (int1 = 0; int1 < stringArray.length - 1; ++int1) {
					kahluaTable = (KahluaTable)kahluaTable.rawget(stringArray[int1]);
				}

				luaClosure = (LuaClosure)kahluaTable.rawget(stringArray[stringArray.length - 1]);
			}

			objectArray = LuaManager.caller.pcall(LuaManager.thread, luaClosure, new Object[]{});
		} else {
			this.paramProper = new Object[this.params.length - 1];
			for (int int2 = 0; int2 < this.params.length; ++int2) {
				String string = this.params[int2];
				kahluaTable = null;
				if (int2 == 0) {
					this.func = this.params[int2].replace("\"", "");
				} else {
					float float1 = 0.0F;
					boolean boolean1 = false;
					try {
						float1 = Float.parseFloat(string);
						boolean1 = true;
					} catch (Exception exception) {
					}

					if (boolean1) {
						this.paramProper[int2 - 1] = new Double((double)float1);
					} else if (string.contains("\"")) {
						this.paramProper[int2 - 1] = string.replace("\"", "");
					} else if (this.currentinstance != null && this.currentinstance.HasAlias(string)) {
						this.paramProper[int2 - 1] = this.currentinstance.getAlias(string);
					} else if (this.module.getCharacter(string) == null) {
						this.paramProper[int2 - 1] = null;
					} else if (this.module.getCharacter(string).Actual == null) {
						this.paramProper[int2 - 1] = null;
					} else {
						this.paramProper[int2 - 1] = this.module.getCharacter(string).Actual;
					}
				}
			}

			luaClosure = null;
			stringArray = this.func.split("\\.");
			kahluaTable = LuaManager.env;
			if (stringArray.length <= 1) {
				luaClosure = (LuaClosure)kahluaTable.rawget(this.func);
			} else {
				for (int1 = 0; int1 < stringArray.length - 1; ++int1) {
					kahluaTable = (KahluaTable)kahluaTable.rawget(stringArray[int1]);
				}

				luaClosure = (LuaClosure)kahluaTable.rawget(stringArray[stringArray.length - 1]);
			}

			objectArray = LuaManager.caller.pcall(LuaManager.thread, luaClosure, (Object[])this.paramProper);
		}

		boolean boolean2 = false;
		if (objectArray.length > 1) {
			boolean2 = (Boolean)objectArray[1];
		}

		if (this.invert) {
			return !boolean2;
		} else {
			return boolean2;
		}
	}

	public void init(String string, String[] stringArray) {
		this.params = stringArray;
		this.val = string;
		if (this.val.indexOf("!") == 0) {
			this.invert = true;
			this.val = this.val.substring(1);
		}
	}

	public void begin() {
		KahluaTable kahluaTable;
		LuaClosure luaClosure;
		String[] stringArray;
		int int1;
		if (this.params.length == 1) {
			luaClosure = null;
			stringArray = this.func.split("\\.");
			kahluaTable = LuaManager.env;
			if (stringArray.length > 1) {
				for (int1 = 0; int1 < stringArray.length - 1; ++int1) {
					kahluaTable = (KahluaTable)kahluaTable.rawget(stringArray[int1]);
				}

				luaClosure = (LuaClosure)kahluaTable.rawget(stringArray[stringArray.length - 1]);
			} else {
				luaClosure = (LuaClosure)kahluaTable.rawget(this.func);
			}

			LuaManager.caller.pcall(LuaManager.thread, luaClosure, new Object[]{});
		} else {
			this.paramProper = new Object[this.params.length - 1];
			for (int int2 = 0; int2 < this.params.length; ++int2) {
				String string = this.params[int2];
				kahluaTable = null;
				if (int2 == 0) {
					this.func = this.params[int2].replace("\"", "");
				} else {
					float float1 = 0.0F;
					boolean boolean1 = false;
					try {
						float1 = Float.parseFloat(string);
						boolean1 = true;
					} catch (Exception exception) {
					}

					if (boolean1) {
						this.paramProper[int2 - 1] = new Double((double)float1);
					} else if (string.contains("\"")) {
						this.paramProper[int2 - 1] = string.replace("\"", "");
					} else if (this.currentinstance != null && this.currentinstance.HasAlias(string)) {
						this.paramProper[int2 - 1] = this.currentinstance.getAlias(string);
					} else if (this.module.getCharacter(string) == null) {
						this.paramProper[int2 - 1] = null;
					} else if (this.module.getCharacter(string).Actual == null) {
						this.paramProper[int2 - 1] = null;
					} else {
						this.paramProper[int2 - 1] = this.module.getCharacter(string).Actual;
					}
				}
			}

			luaClosure = null;
			stringArray = this.func.split("\\.");
			kahluaTable = LuaManager.env;
			if (stringArray.length > 1) {
				for (int1 = 0; int1 < stringArray.length - 1; ++int1) {
					kahluaTable = (KahluaTable)kahluaTable.rawget(stringArray[int1]);
				}

				luaClosure = (LuaClosure)kahluaTable.rawget(stringArray[stringArray.length - 1]);
			} else {
				luaClosure = (LuaClosure)kahluaTable.rawget(this.func);
			}

			LuaManager.caller.pcall(LuaManager.thread, luaClosure, (Object[])this.paramProper);
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}
}
