package se.krka.kahlua.vm;

import java.io.File;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.gameStates.ChooseGameInfo;


public final class LuaClosure {
	public Prototype prototype;
	public KahluaTable env;
	public UpValue[] upvalues;
	public String debugName;

	public LuaClosure(Prototype prototype, KahluaTable kahluaTable) {
		this.prototype = prototype;
		if (LuaCompiler.rewriteEvents) {
			LuaEventManager.reroute(prototype, this);
			MapObjects.reroute(prototype, this);
		}

		this.env = kahluaTable;
		this.upvalues = new UpValue[prototype.numUpvalues];
	}

	public String toString() {
		if (this.prototype.lines.length > 0) {
			String string = this.prototype.toString();
			return "function " + string + ":" + this.prototype.lines[0];
		} else {
			int int1 = this.hashCode();
			return "function[" + Integer.toString(int1, 36) + "]";
		}
	}

	public String toString2(int int1) {
		if (this.prototype.lines.length > 0) {
			if (int1 == 0) {
				int1 = 1;
			}

			if (this.prototype.filename == null) {
				return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[int1 - 1];
			} else {
				String string = " | Vanilla";
				String string2 = this.prototype.filename;
				string2 = string2.replace("/", File.separator);
				if (string2.contains(File.separator + "mods" + File.separator)) {
					String string3 = string2.substring(0, string2.indexOf(File.separator + "media"));
					ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.getModInfoForDir(string3);
					string = " | MOD: " + mod.getName();
					KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("PauseBuggedModList");
					if (kahluaTable != null) {
						kahluaTable.rawset(mod.getName(), true);
					}
				}

				return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[int1 - 1] + string;
			}
		} else {
			int int2 = this.hashCode();
			return "function[" + Integer.toString(int2, 36) + "]";
		}
	}
}
