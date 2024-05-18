package zombie.characters.CharacterTimedActions;

import se.krka.kahlua.vm.KahluaTable;
import zombie.PathfindManager;
import zombie.Lua.LuaManager;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.characters.IsoGameCharacter;


public class LuaTimedActionNew extends BaseAction implements IPathfinder {
	KahluaTable table;

	public LuaTimedActionNew(KahluaTable kahluaTable, IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.table = kahluaTable;
		Object object = kahluaTable.rawget("maxTime");
		this.MaxTime = (Integer)LuaManager.converterManager.fromLuaToJava(object, Integer.class);
		Object object2 = kahluaTable.rawget("stopOnWalk");
		Object object3 = kahluaTable.rawget("stopOnRun");
		Object object4 = kahluaTable.rawget("stopOnAim");
		Object object5 = kahluaTable.rawget("caloriesModifier");
		if (object2 != null) {
			this.StopOnWalk = (Boolean)LuaManager.converterManager.fromLuaToJava(object2, Boolean.class);
		}

		if (object3 != null) {
			this.StopOnRun = (Boolean)LuaManager.converterManager.fromLuaToJava(object3, Boolean.class);
		}

		if (object4 != null) {
			this.StopOnAim = (Boolean)LuaManager.converterManager.fromLuaToJava(object4, Boolean.class);
		}

		if (object5 != null) {
			this.caloriesModifier = (Float)LuaManager.converterManager.fromLuaToJava(object5, Float.class);
		}
	}

	public void update() {
		super.update();
		LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("update"), (Object)this.table);
	}

	public boolean valid() {
		Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("isValid"), (Object)this.table);
		return objectArray.length > 1 && objectArray[1] instanceof Boolean && (Boolean)((Boolean)objectArray[1]);
	}

	public void start() {
		this.CurrentTime = 0.0F;
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("start"), (Object)this.table);
	}

	public void stop() {
		super.stop();
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("stop"), (Object)this.table);
	}

	public void perform() {
		LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("perform"), (Object)this.table);
	}

	public void Failed(Mover mover) {
		this.table.rawset("path", (Object)null);
		LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("failedPathfind"), (Object)this.table);
	}

	public void Succeeded(Path path, Mover mover) {
		this.table.rawset("path", path);
		LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("succeededPathfind"), (Object)this.table);
	}

	public void Pathfind(IsoGameCharacter gameCharacter, int int1, int int2, int int3) {
		PathfindManager.instance.AddJob(this, gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), int1, int2, int3);
	}

	public String getName() {
		return "timedActionPathfind";
	}

	public void setTime(int int1) {
		this.MaxTime = int1;
	}
}
