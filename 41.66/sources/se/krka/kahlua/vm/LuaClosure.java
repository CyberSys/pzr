package se.krka.kahlua.vm;

import se.krka.kahlua.luaj.compiler.LuaCompiler;
import zombie.Lua.LuaEventManager;
import zombie.Lua.MapObjects;


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

			return "function: " + this.prototype.name + " -- file: " + this.prototype.file + " line # " + this.prototype.lines[int1 - 1];
		} else {
			int int2 = this.hashCode();
			return "function[" + Integer.toString(int2, 36) + "]";
		}
	}
}
