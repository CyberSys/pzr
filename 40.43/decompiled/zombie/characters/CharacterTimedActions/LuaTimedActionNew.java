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

   public LuaTimedActionNew(KahluaTable var1, IsoGameCharacter var2) {
      super(var2);
      this.table = var1;
      Object var3 = var1.rawget("maxTime");
      this.MaxTime = (Integer)LuaManager.converterManager.fromLuaToJava(var3, Integer.class);
      Object var4 = var1.rawget("stopOnWalk");
      Object var5 = var1.rawget("stopOnRun");
      Object var6 = var1.rawget("stopOnAim");
      Object var7 = var1.rawget("caloriesModifier");
      if (var4 != null) {
         this.StopOnWalk = (Boolean)LuaManager.converterManager.fromLuaToJava(var4, Boolean.class);
      }

      if (var5 != null) {
         this.StopOnRun = (Boolean)LuaManager.converterManager.fromLuaToJava(var5, Boolean.class);
      }

      if (var6 != null) {
         this.StopOnAim = (Boolean)LuaManager.converterManager.fromLuaToJava(var6, Boolean.class);
      }

      if (var7 != null) {
         this.caloriesModifier = (Float)LuaManager.converterManager.fromLuaToJava(var7, Float.class);
      }

   }

   public void update() {
      super.update();
      LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("update"), (Object)this.table);
   }

   public boolean valid() {
      Object[] var1 = LuaManager.caller.pcall(LuaManager.thread, this.table.rawget("isValid"), (Object)this.table);
      return var1.length > 1 && var1[1] instanceof Boolean && (Boolean)((Boolean)var1[1]);
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

   public void Failed(Mover var1) {
      this.table.rawset("path", (Object)null);
      LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("failedPathfind"), (Object)this.table);
   }

   public void Succeeded(Path var1, Mover var2) {
      this.table.rawset("path", var1);
      LuaManager.caller.pcallvoid(LuaManager.thread, this.table.rawget("succeededPathfind"), (Object)this.table);
   }

   public void Pathfind(IsoGameCharacter var1, int var2, int var3, int var4) {
      PathfindManager.instance.AddJob(this, var1, (int)var1.getX(), (int)var1.getY(), (int)var1.getZ(), var2, var3, var4);
   }

   public String getName() {
      return "timedActionPathfind";
   }

   public void setTime(int var1) {
      this.MaxTime = var1;
   }
}
