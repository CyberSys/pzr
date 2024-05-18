package zombie.ui;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;

public class LuaUIWindow extends NewWindow {
   public LuaUIWindow(int var1, int var2, int var3, int var4, boolean var5, KahluaTable var6) {
      super(var1, var2, var3, var4, var5);
      this.ResizeToFitY = false;
      this.table = var6;
   }

   public void ButtonClicked(String var1) {
      super.ButtonClicked(var1);
      if (this.getTable().rawget("onButtonClicked") != null) {
         Object[] var2 = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onButtonClicked"), this.table, var1);
      }

   }
}
