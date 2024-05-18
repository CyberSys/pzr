package zombie.Quests;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;

public class QuestTask_LuaCondition extends QuestTask {
   KahluaTable table;
   LuaClosure ArbAction;

   public QuestTask_LuaCondition(String var1, String var2, LuaClosure var3, KahluaTable var4) {
      super(QuestTaskType.Custom, var1, var2);
      this.table = var4;
      this.ArbAction = var3;
   }

   public void Update() {
      if (!this.Complete && this.ArbActionCheck()) {
         this.Complete = true;
      }

      super.Update();
   }

   private boolean ArbActionCheck() {
      Object[] var1 = LuaManager.caller.pcall(LuaManager.thread, this.ArbAction, (Object)this.table);
      return var1.length > 1 ? (Boolean)var1[1] : false;
   }
}
