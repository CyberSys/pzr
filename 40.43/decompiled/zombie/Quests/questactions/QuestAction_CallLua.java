package zombie.Quests.questactions;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;

public class QuestAction_CallLua implements QuestAction {
   LuaClosure function;
   KahluaTable table;

   public QuestAction_CallLua(LuaClosure var1, KahluaTable var2) {
      this.function = var1;
      this.table = var2;
   }

   public void Execute() {
      LuaManager.caller.pcallvoid(LuaManager.thread, this.function, (Object)this.table);
   }
}
